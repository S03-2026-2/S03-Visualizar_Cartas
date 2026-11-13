import { Link, useParams } from 'react-router-dom'
import styles from '../styles/layout.module.css'

export default function CardDetailPage() {
  const { id } = useParams<{ id: string }>()

  return (
    <div className={styles.shell}>
      <header className={styles.header}>
        <div className={styles.headerInner}>
          <span className={styles.brand}>
            <span className={styles.brandMark} aria-hidden="true">
              ◈
            </span>
            Visualizar Cartas
          </span>
        </div>
      </header>

      <main id="conteudo" className={styles.main}>
        <Link className={styles.backLink} to="/cartas">
          ← Voltar para a coleção
        </Link>
        <h1 className={styles.pageTitle}>Detalhe da carta</h1>
        <p className={styles.placeholder}>
          Carta #{id}: o detalhe completo será implementado na próxima etapa.
        </p>
      </main>
    </div>
  )
}
