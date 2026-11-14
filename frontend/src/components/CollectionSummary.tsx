import type { Resumo } from '../utils'
import styles from '../styles/cards.module.css'

type CollectionSummaryProps = {
  resumo: Resumo
}

export default function CollectionSummary({ resumo }: CollectionSummaryProps) {
  return (
    <section className={styles.summary} aria-label="Resumo da coleção">
      <div className={styles.summaryTile}>
        <span className={styles.summaryLabel}>Total de cartas</span>
        <strong className={styles.summaryValue}>{resumo.total}</strong>
      </div>

      <div className={styles.summaryTile}>
        <span className={styles.summaryLabel}>Tipos</span>
        <strong className={styles.summaryValue}>{resumo.tipos.length}</strong>
      </div>

      <div className={styles.summaryTile}>
        <span className={styles.summaryLabel}>Experiência média</span>
        <strong className={styles.summaryValue}>{resumo.experienciaMedia}</strong>
      </div>
    </section>
  )
}
