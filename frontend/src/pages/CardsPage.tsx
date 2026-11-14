import { useEffect, useMemo, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { ApiError, listarCartas } from '../api/client'
import type { Carta } from '../api/types'
import { useAuth } from '../auth/AuthContext'
import CardTile from '../components/CardTile'
import CardToolbar from '../components/CardToolbar'
import CollectionSummary from '../components/CollectionSummary'
import {
  EmptyState,
  ErrorState,
  LoadingGrid,
  NoResultsState,
} from '../components/States'
import { useRequest } from '../hooks/useRequest'
import {
  filterCards,
  sortCards,
  summarize,
  typeLabel,
  type Ordem,
} from '../utils'
import cardsStyles from '../styles/cards.module.css'
import layout from '../styles/layout.module.css'

const ORDENS_VALIDAS: readonly Ordem[] = [
  'original',
  'nome-asc',
  'nome-desc',
  'exp-desc',
  'exp-asc',
]

const DEBOUNCE_MS = 200

function lerTipos(params: URLSearchParams): string[] {
  const bruto = params.get('tipos')

  if (!bruto) {
    return []
  }

  return [
    ...new Set(
      bruto
        .split(',')
        .map((tipo) => tipo.trim())
        .filter(Boolean),
    ),
  ]
}

function lerOrdem(params: URLSearchParams): Ordem {
  const bruto = params.get('ordem')

  if (bruto && (ORDENS_VALIDAS as readonly string[]).includes(bruto)) {
    return bruto as Ordem
  }

  return 'original'
}

export default function CardsPage() {
  const { usuario, logout } = useAuth()
  const [searchParams, setSearchParams] = useSearchParams()

  const [busca, setBusca] = useState(() => searchParams.get('busca') ?? '')
  const [buscaDebounced, setBuscaDebounced] = useState(busca)
  const [tiposSelecionados, setTiposSelecionados] = useState<string[]>(() =>
    lerTipos(searchParams),
  )
  const [ordem, setOrdem] = useState<Ordem>(() => lerOrdem(searchParams))

  const { data, error, status, refetch } = useRequest(
    usuario ? `cartas:${usuario.id}` : 'cartas:sem-usuario',
    (signal) =>
      usuario
        ? listarCartas(usuario.id, signal)
        : Promise.resolve<Carta[]>([]),
  )

  useEffect(() => {
    const timer = window.setTimeout(() => setBuscaDebounced(busca), DEBOUNCE_MS)
    return () => window.clearTimeout(timer)
  }, [busca])

  useEffect(() => {
    const proximo = new URLSearchParams(searchParams)
    const termo = buscaDebounced.trim()

    if (termo) {
      proximo.set('busca', termo)
    } else {
      proximo.delete('busca')
    }

    if (tiposSelecionados.length > 0) {
      proximo.set('tipos', tiposSelecionados.join(','))
    } else {
      proximo.delete('tipos')
    }

    if (ordem !== 'original') {
      proximo.set('ordem', ordem)
    } else {
      proximo.delete('ordem')
    }

    if (proximo.toString() !== searchParams.toString()) {
      setSearchParams(proximo, { replace: true })
    }
  }, [buscaDebounced, tiposSelecionados, ordem, searchParams, setSearchParams])

  useEffect(() => {
    if (error instanceof ApiError && error.status === 401) {
      logout()
    }
  }, [error, logout])

  const cartas = useMemo(() => data ?? [], [data])

  const tiposDisponiveis = useMemo(() => {
    const nomes = new Set<string>()

    for (const carta of cartas) {
      for (const tipo of carta.tipos ?? []) {
        nomes.add(tipo.nome)
      }
    }

    return [...nomes].sort((a, b) =>
      typeLabel(a).localeCompare(typeLabel(b), 'pt-BR'),
    )
  }, [cartas])

  const cartasFiltradas = useMemo(
    () => sortCards(filterCards(cartas, buscaDebounced, tiposSelecionados), ordem),
    [cartas, buscaDebounced, tiposSelecionados, ordem],
  )

  const resumo = useMemo(() => summarize(cartas), [cartas])

  function alternarTipo(tipo: string) {
    setTiposSelecionados((atuais) =>
      atuais.includes(tipo)
        ? atuais.filter((atual) => atual !== tipo)
        : [...atuais, tipo],
    )
  }

  function limparFiltros() {
    setBusca('')
    setBuscaDebounced('')
    setTiposSelecionados([])
    setOrdem('original')
  }

  if (!usuario) {
    return null
  }

  const carregando = status === 'idle' || status === 'loading'

  return (
    <div className={layout.shell}>
      <header className={layout.header}>
        <div className={layout.headerInner}>
          <span className={layout.brand}>
            <span className={layout.brandMark} aria-hidden="true">
              ◈
            </span>
            Visualizar Cartas
          </span>

          <div className={layout.headerMeta}>
            <span className={layout.usuario}>
              <span className={layout.usuarioNome}>{usuario.nome}</span>
              <span className={layout.ranking}>ranking #{usuario.ranking}</span>
            </span>
            <button className={layout.logout} type="button" onClick={logout}>
              Sair
            </button>
          </div>
        </div>
      </header>

      <main id="conteudo" className={layout.main}>
        <div className={layout.pageHeader}>
          <div>
            <h1 className={layout.pageTitle}>Sua coleção</h1>
            <p className={layout.pageSubtitle}>
              Busque, filtre e explore as cartas do seu treinador.
            </p>
          </div>
        </div>

        {carregando && <LoadingGrid />}

        {!carregando && status === 'error' && (
          <ErrorState onRetry={refetch} />
        )}

        {status === 'success' && cartas.length === 0 && <EmptyState />}

        {status === 'success' && cartas.length > 0 && (
          <>
            <CollectionSummary resumo={resumo} />

            <CardToolbar
              cartas={cartas}
              tipos={tiposDisponiveis}
              busca={busca}
              tiposSelecionados={tiposSelecionados}
              ordem={ordem}
              resultado={cartasFiltradas.length}
              onBusca={setBusca}
              onToggleTipo={alternarTipo}
              onOrdem={setOrdem}
              onLimpar={limparFiltros}
            />

            {cartasFiltradas.length === 0 ? (
              <NoResultsState onLimpar={limparFiltros} />
            ) : (
              <div className={cardsStyles.grid}>
                {cartasFiltradas.map((carta, index) => (
                  <CardTile key={carta.id} carta={carta} index={index} />
                ))}
              </div>
            )}
          </>
        )}
      </main>
    </div>
  )
}
