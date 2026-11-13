import {
  useMemo,
  useRef,
  useState,
  type MouseEvent as ReactMouseEvent,
} from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { detalharCarta } from '../api/client'
import type { Carta } from '../api/types'
import { useAuth } from '../auth/AuthContext'
import SpriteCarousel from '../components/SpriteCarousel'
import { useHolo } from '../hooks/useHolo'
import { useRequest } from '../hooks/useRequest'
import {
  formatAltura,
  formatNome,
  formatPeso,
  selectSprites,
  typeLabel,
} from '../utils'
import detail from '../styles/detail.module.css'
import holo from '../styles/holo.module.css'
import layout from '../styles/layout.module.css'

function numeroCarta(id: number): string {
  return `#${String(id).padStart(3, '0')}`
}

/** Volta pelo histórico quando existe; senão usa `/cartas` preservando a query. */
function useVoltarParaColecao() {
  const navigate = useNavigate()
  const location = useLocation()
  const to = `/cartas${location.search}`

  function onClick(event: ReactMouseEvent<HTMLAnchorElement>) {
    const state = window.history.state as { idx?: unknown } | null
    const posicao = typeof state?.idx === 'number' ? state.idx : 0

    const navegacaoSimples =
      event.button === 0 && !event.metaKey && !event.ctrlKey && !event.shiftKey

    if (posicao > 0 && navegacaoSimples) {
      event.preventDefault()
      navigate(-1)
    }
  }

  return { to, onClick }
}

function DetailHeader() {
  const { usuario } = useAuth()

  return (
    <header className={layout.header}>
      <div className={layout.headerInner}>
        <span className={layout.brand}>
          <span className={layout.brandMark} aria-hidden="true">
            ◈
          </span>
          Visualizar Cartas
        </span>

        {usuario && (
          <div className={layout.headerMeta}>
            <span className={layout.usuario}>
              <span className={layout.usuarioNome}>{usuario.nome}</span>
              <span className={layout.ranking}>ranking #{usuario.ranking}</span>
            </span>
          </div>
        )}
      </div>
    </header>
  )
}

function CartaInvalida({ to }: { to: string }) {
  return (
    <div className={detail.stateBlock}>
      <div className={detail.stateAlert} role="alert">
        <h1 className={detail.stateTitle}>Carta não encontrada</h1>
        <p className={detail.stateText}>
          O identificador desta carta é inválido. Escolha uma carta da sua
          coleção para continuar.
        </p>
      </div>
      <Link className={detail.stateAction} to={to}>
        Voltar para a coleção
      </Link>
    </div>
  )
}

function DetailSkeleton() {
  return (
    <div className={detail.loading} aria-busy="true">
      <p className="visually-hidden" role="status">
        Carregando carta…
      </p>
      <h1 className="visually-hidden">Detalhe da carta</h1>

      <div className={detail.skeletonLayout} aria-hidden="true">
        <div className={detail.skeletonFrame} />
        <div className={detail.skeletonInfo}>
          <div className={detail.skeletonEyebrow} />
          <div className={detail.skeletonTitle} />
          <div className={detail.skeletonLine} />
          <div className={detail.skeletonLineShort} />
          <div className={detail.skeletonBlock} />
        </div>
      </div>
    </div>
  )
}

function DetailError({ onRetry }: { onRetry: () => void }) {
  return (
    <div className={detail.stateBlock}>
      <div className={detail.stateAlert} role="alert">
        <h1 className={detail.stateTitle}>Não foi possível carregar esta carta</h1>
        <p className={detail.stateText}>
          Verifique sua conexão e tente novamente em instantes.
        </p>
      </div>
      <button className={detail.stateAction} type="button" onClick={onRetry}>
        Tentar novamente
      </button>
    </div>
  )
}

function CardDetailContent({ carta }: { carta: Carta }) {
  const nome = formatNome(carta.nome)
  const tipos = carta.tipos ?? []
  const habilidades = carta.habilidades ?? []
  const sprites = useMemo(() => selectSprites(carta), [carta])
  const onPointerMove = useHolo<HTMLDivElement>()
  const tipoPrimario = tipos[0]?.nome
  const urlCry = carta.urlCry?.trim() ?? ''

  const audioRef = useRef<HTMLAudioElement>(null)
  const [cryStatus, setCryStatus] = useState<'idle' | 'playing' | 'error'>('idle')

  async function tocarCry() {
    const audio = audioRef.current

    if (!audio || !urlCry) {
      return
    }

    setCryStatus('idle')

    try {
      audio.currentTime = 0
      await audio.play()
      setCryStatus('playing')
    } catch {
      setCryStatus('error')
    }
  }

  return (
    <article className={detail.layout} aria-labelledby="carta-titulo">
      <div className={detail.artColumn}>
        <div
          className={`${detail.frame} ${holo.holo}`}
          data-tipo={tipoPrimario}
          onPointerMove={onPointerMove}
        >
          <div className={detail.frameTop} aria-hidden="true">
            <span>{numeroCarta(carta.id)}</span>
            <span>{tipoPrimario ? typeLabel(tipoPrimario) : 'Carta'}</span>
          </div>

          <div className={detail.frameArt}>
            <SpriteCarousel sprites={sprites} alt={nome} />
          </div>

          <div className={detail.frameFooter} aria-hidden="true">
            {tipos.length > 0
              ? tipos.map((tipo) => typeLabel(tipo.nome)).join(' · ')
              : 'Pokémon'}
          </div>
        </div>
      </div>

      <div className={detail.info}>
        <p className={detail.eyebrow}>Carta {numeroCarta(carta.id)}</p>
        <h1 id="carta-titulo" className={detail.title}>
          {nome}
        </h1>

        {tipos.length > 0 && (
          <ul className={detail.chips} aria-label="Tipos">
            {tipos.map((tipo) => (
              <li
                key={`${tipo.id}-${tipo.nome}`}
                className={detail.chip}
                data-tipo={tipo.nome}
              >
                {typeLabel(tipo.nome)}
              </li>
            ))}
          </ul>
        )}

        <dl className={detail.quickStats}>
          <div>
            <dt>Experiência</dt>
            <dd>{carta.experience}</dd>
          </div>
          <div>
            <dt>Altura</dt>
            <dd>{formatAltura(carta.altura)}</dd>
          </div>
          <div>
            <dt>Peso</dt>
            <dd>{formatPeso(carta.peso)}</dd>
          </div>
        </dl>

        <section className={detail.section} aria-labelledby="habilidades-titulo">
          <h2 id="habilidades-titulo" className={detail.sectionTitle}>
            Habilidades
          </h2>

          {habilidades.length > 0 ? (
            <ul className={detail.abilities}>
              {habilidades.map((habilidade) => (
                <li
                  key={`${habilidade.id}-${habilidade.nome}`}
                  className={detail.ability}
                >
                  <span className={detail.abilityName}>{habilidade.nome}</span>
                  {habilidade.geracao && (
                    <span className={detail.abilityGeneration}>
                      {habilidade.geracao}
                    </span>
                  )}
                </li>
              ))}
            </ul>
          ) : (
            <p className={detail.muted}>Nenhuma habilidade registrada.</p>
          )}
        </section>

        <section className={detail.section} aria-labelledby="dados-titulo">
          <h2 id="dados-titulo" className={detail.sectionTitle}>
            Dados do Pokémon
          </h2>

          <dl className={detail.dataList}>
            <div>
              <dt>ID</dt>
              <dd>{numeroCarta(carta.id)}</dd>
            </div>
            <div>
              <dt>Experiência</dt>
              <dd>{carta.experience}</dd>
            </div>
          </dl>
        </section>

        <div className={detail.cry}>
          <button
            className={detail.cryButton}
            type="button"
            onClick={tocarCry}
            disabled={!urlCry}
          >
            {urlCry ? 'Ouvir cry' : 'Cry indisponível'}
          </button>

          {urlCry && (
            <audio
              ref={audioRef}
              className={detail.audio}
              src={urlCry}
              preload="none"
              onEnded={() => setCryStatus('idle')}
              onError={() => setCryStatus('error')}
            />
          )}

          {cryStatus === 'playing' && (
            <p className={detail.cryStatus} role="status">
              Reproduzindo cry…
            </p>
          )}

          {cryStatus === 'error' && (
            <p className={detail.cryStatus} role="status">
              Áudio indisponível neste navegador.
            </p>
          )}
        </div>
      </div>
    </article>
  )
}

function DetalheCarta({ id }: { id: number }) {
  const { data, status, refetch } = useRequest(`carta:${id}`, (signal) =>
    detalharCarta(id, signal),
  )

  if (status === 'idle' || status === 'loading') {
    return <DetailSkeleton />
  }

  if (status === 'error' || !data) {
    return <DetailError onRetry={refetch} />
  }

  return <CardDetailContent carta={data} />
}

export default function CardDetailPage() {
  const { id: idParam } = useParams<{ id: string }>()
  const voltar = useVoltarParaColecao()

  const id = Number(idParam)
  const idValido = Number.isInteger(id) && id > 0

  return (
    <div className={layout.shell}>
      <DetailHeader />

      <main id="conteudo" className={layout.main}>
        <Link
          className={layout.backLink}
          to={voltar.to}
          onClick={voltar.onClick}
        >
          ← Voltar para a coleção
        </Link>

        {idValido ? <DetalheCarta id={id} /> : <CartaInvalida to={voltar.to} />}
      </main>
    </div>
  )
}
