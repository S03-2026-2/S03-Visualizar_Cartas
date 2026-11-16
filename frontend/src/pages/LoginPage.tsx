import { useState, type FormEvent } from 'react'
import { Navigate, useLocation, useNavigate } from 'react-router-dom'
import { ApiError } from '../api/client'
import { useAuth } from '../auth/AuthContext'
import styles from '../styles/login.module.css'

type LocationState = { from?: string } | null

function destinoDe(state: unknown): string {
  const from = (state as LocationState)?.from
  return from && from.startsWith('/') ? from : '/cartas'
}

export default function LoginPage() {
  const { usuario, login } = useAuth()
  const navigate = useNavigate()
  const location = useLocation()

  const [email, setEmail] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState<string | null>(null)
  const [enviando, setEnviando] = useState(false)

  if (usuario) {
    return <Navigate to="/cartas" replace />
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()
    setErro(null)
    setEnviando(true)

    try {
      await login(email.trim(), senha)
      navigate(destinoDe(location.state), { replace: true })
    } catch (error) {
      if (error instanceof ApiError && error.status === 401) {
        setErro('E-mail ou senha inválidos.')
      } else {
        setErro('Não foi possível entrar agora. Tente novamente em instantes.')
      }
    } finally {
      setEnviando(false)
    }
  }

  return (
    <main id="conteudo" className={styles.page}>
      <div className={styles.layout}>
        <div className={styles.showcase} aria-hidden="true">
          <div className={styles.holoCard}>
            <div className={styles.holoTop}>
              <span>#001</span>
              <span className={styles.holoType}>TCG</span>
            </div>
            <span className={styles.holoMonogram}>VC</span>
            <div className={styles.holoBottom}>
              <span className={styles.holoName}>Visualizar Cartas</span>
              <span className={styles.holoRule} />
              <div className={styles.holoMeta}>
                <span>Coleção</span>
                <span>Holo</span>
              </div>
            </div>
          </div>
        </div>

        <section className={styles.panel} aria-labelledby="login-title">
          <p className={styles.eyebrow}>Visualizar Cartas</p>
          <h1 id="login-title" className={styles.title}>
            Entrar
          </h1>
          <p className={styles.subtitle}>Acesse sua coleção de cartas.</p>

          <form className={styles.form} onSubmit={handleSubmit} noValidate>
            {erro && (
              <p id="login-erro" className={styles.alert} role="alert">
                {erro}
              </p>
            )}

            <div className={styles.field}>
              <label className={styles.label} htmlFor="email">
                E-mail
              </label>
              <input
                id="email"
                name="email"
                className={styles.input}
                type="email"
                autoComplete="email"
                value={email}
                onChange={(event) => setEmail(event.target.value)}
                aria-invalid={erro ? true : undefined}
                aria-describedby={erro ? 'login-erro' : undefined}
                required
              />
            </div>

            <div className={styles.field}>
              <label className={styles.label} htmlFor="senha">
                Senha
              </label>
              <input
                id="senha"
                name="senha"
                className={styles.input}
                type="password"
                autoComplete="current-password"
                value={senha}
                onChange={(event) => setSenha(event.target.value)}
                aria-invalid={erro ? true : undefined}
                aria-describedby={erro ? 'login-erro' : undefined}
                required
              />
            </div>

            <button className={styles.submit} type="submit" disabled={enviando}>
              {enviando ? 'Entrando…' : 'Entrar'}
            </button>
          </form>

          <p className={styles.footnote}>
            Sessão local para fins acadêmicos. Nenhuma senha é armazenada no navegador.
          </p>
        </section>
      </div>
    </main>
  )
}
