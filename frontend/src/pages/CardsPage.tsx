import { useAuth } from '../auth/AuthContext'
import styles from '../styles/layout.module.css'

export default function CardsPage() {
  const { usuario, logout } = useAuth()

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

          <div className={styles.headerMeta}>
            {usuario && (
              <span className={styles.usuario}>
                <span className={styles.usuarioNome}>{usuario.nome}</span>
                <span className={styles.ranking}>ranking #{usuario.ranking}</span>
              </span>
            )}
            <button className={styles.logout} type="button" onClick={logout}>
              Sair
            </button>
          </div>
        </div>
      </header>

      <main id="conteudo" className={styles.main}>
        <h1 className={styles.pageTitle}>Sua coleção</h1>
      </main>
    </div>
  )
}
