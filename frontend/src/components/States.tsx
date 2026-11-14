import styles from '../styles/cards.module.css'

export function LoadingGrid() {
  return (
    <div className={styles.loadingRegion} aria-busy="true">
      <p className="visually-hidden" role="status">
        Carregando cartas…
      </p>

      <div className={styles.skeletonGrid} aria-hidden="true">
        {Array.from({ length: 6 }, (_, indice) => (
          <div key={indice} className={styles.skeletonCard}>
            <div className={styles.skeletonArt} />
            <div className={styles.skeletonLine} />
            <div className={styles.skeletonLineShort} />
          </div>
        ))}
      </div>
    </div>
  )
}

type ErrorStateProps = {
  mensagem?: string
  onRetry: () => void
}

export function ErrorState({
  mensagem = 'Não foi possível carregar sua coleção agora.',
  onRetry,
}: ErrorStateProps) {
  return (
    <div className={styles.stateBlock}>
      <div className={styles.stateAlert} role="alert">
        <h2 className={styles.stateTitle}>Algo deu errado</h2>
        <p className={styles.stateText}>
          {mensagem} Verifique sua conexão e tente novamente.
        </p>
      </div>

      <button className={styles.stateAction} type="button" onClick={onRetry}>
        Tentar novamente
      </button>
    </div>
  )
}

export function EmptyState() {
  return (
    <div className={styles.stateBlock}>
      <h2 className={styles.stateTitle}>Sua coleção está vazia</h2>
      <p className={styles.stateText}>
        Ainda não há cartas distribuídas para este treinador. Assim que novas
        cartas chegarem, elas aparecerão aqui.
      </p>
    </div>
  )
}

type NoResultsStateProps = {
  onLimpar: () => void
}

export function NoResultsState({ onLimpar }: NoResultsStateProps) {
  return (
    <div className={styles.stateBlock}>
      <h2 className={styles.stateTitle}>Nenhuma carta encontrada</h2>
      <p className={styles.stateText}>
        Nenhuma carta da sua coleção combina com a busca e os filtros atuais.
        Tente outro termo ou remova os filtros.
      </p>

      <button className={styles.stateAction} type="button" onClick={onLimpar}>
        Limpar filtros
      </button>
    </div>
  )
}
