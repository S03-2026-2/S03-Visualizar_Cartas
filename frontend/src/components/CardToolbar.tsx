import type { Carta } from '../api/types'
import { typeLabel, type Ordem } from '../utils'
import styles from '../styles/cards.module.css'

type CardToolbarProps = {
  cartas: Carta[]
  tipos: string[]
  busca: string
  tiposSelecionados: string[]
  ordem: Ordem
  resultado: number
  onBusca: (valor: string) => void
  onToggleTipo: (tipo: string) => void
  onOrdem: (ordem: Ordem) => void
  onLimpar: () => void
}

const ORDENS: readonly { valor: Ordem; rotulo: string }[] = [
  { valor: 'original', rotulo: 'Ordem original' },
  { valor: 'nome-asc', rotulo: 'Nome (A–Z)' },
  { valor: 'nome-desc', rotulo: 'Nome (Z–A)' },
  { valor: 'exp-desc', rotulo: 'Experiência (maior)' },
  { valor: 'exp-asc', rotulo: 'Experiência (menor)' },
]

function isOrdem(valor: string): valor is Ordem {
  return ORDENS.some((opcao) => opcao.valor === valor)
}

export default function CardToolbar({
  cartas,
  tipos,
  busca,
  tiposSelecionados,
  ordem,
  resultado,
  onBusca,
  onToggleTipo,
  onOrdem,
  onLimpar,
}: CardToolbarProps) {
  const filtrosAtivos =
    busca.trim() !== '' ||
    tiposSelecionados.length > 0 ||
    ordem !== 'original'

  return (
    <section className={styles.toolbar} aria-label="Busca e filtros">
      <div className={styles.toolbarRow}>
        <div className={styles.searchField}>
          <span className={styles.searchIcon} aria-hidden="true">
            ⌕
          </span>
          <input
            className={styles.searchInput}
            type="search"
            name="busca"
            aria-label="Buscar Pokémon"
            placeholder="Buscar por nome…"
            autoComplete="off"
            value={busca}
            onChange={(event) => onBusca(event.target.value)}
          />
        </div>

        <span className={styles.selectWrap}>
          <select
            className={styles.select}
            aria-label="Ordenar"
            value={ordem}
            onChange={(event) => {
              const valor = event.target.value
              if (isOrdem(valor)) {
                onOrdem(valor)
              }
            }}
          >
            {ORDENS.map((opcao) => (
              <option key={opcao.valor} value={opcao.valor}>
                {opcao.rotulo}
              </option>
            ))}
          </select>
        </span>
      </div>

      {tipos.length > 0 && (
        <div className={styles.typeFilter} role="group" aria-label="Filtrar por tipo">
          {tipos.map((tipo) => (
            <button
              key={tipo}
              className={styles.chip}
              data-tipo={tipo}
              type="button"
              aria-pressed={tiposSelecionados.includes(tipo)}
              onClick={() => onToggleTipo(tipo)}
            >
              {typeLabel(tipo)}
            </button>
          ))}
        </div>
      )}

      <div className={styles.toolbarFooter}>
        <p className={styles.resultCount} aria-live="polite">
          {resultado} de {cartas.length} cartas
        </p>

        {filtrosAtivos && (
          <button className={styles.clearButton} type="button" onClick={onLimpar}>
            Limpar filtros
          </button>
        )}
      </div>
    </section>
  )
}
