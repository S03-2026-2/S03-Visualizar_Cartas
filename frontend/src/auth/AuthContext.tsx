import {
  createContext,
  useCallback,
  useContext,
  useMemo,
  useState,
  type ReactNode,
} from 'react'
import { login as loginRequest } from '../api/client'
import type { Usuario } from '../api/types'

const STORAGE_KEY = 'vc.user.v1'

type AuthState = {
  usuario: Usuario | null
  login(email: string, senha: string): Promise<void>
  logout(): void
}

const AuthContext = createContext<AuthState | null>(null)

function isUsuario(value: unknown): value is Usuario {
  if (typeof value !== 'object' || value === null) {
    return false
  }

  const candidato = value as Partial<Usuario>

  return (
    typeof candidato.id === 'number' &&
    typeof candidato.nome === 'string' &&
    typeof candidato.ranking === 'number'
  )
}

function lerUsuario(): Usuario | null {
  try {
    const bruto = window.localStorage.getItem(STORAGE_KEY)

    if (!bruto) {
      return null
    }

    const parsed: unknown = JSON.parse(bruto)

    if (!isUsuario(parsed)) {
      window.localStorage.removeItem(STORAGE_KEY)
      return null
    }

    return parsed
  } catch {
    try {
      window.localStorage.removeItem(STORAGE_KEY)
    } catch {
      // Storage indisponível: segue sem sessão.
    }
    return null
  }
}

function persistir(usuario: Usuario): void {
  try {
    window.localStorage.setItem(STORAGE_KEY, JSON.stringify(usuario))
  } catch {
    // Falha ao persistir não deve impedir a sessão em memória.
  }
}

function remover(): void {
  try {
    window.localStorage.removeItem(STORAGE_KEY)
  } catch {
    // Storage indisponível: nada a remover.
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [usuario, setUsuario] = useState<Usuario | null>(lerUsuario)

  const login = useCallback(async (email: string, senha: string) => {
    const autenticado = await loginRequest(email, senha)
    persistir(autenticado)
    setUsuario(autenticado)
  }, [])

  const logout = useCallback(() => {
    remover()
    setUsuario(null)
  }, [])

  const value = useMemo<AuthState>(
    () => ({ usuario, login, logout }),
    [usuario, login, logout],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth(): AuthState {
  const contexto = useContext(AuthContext)

  if (!contexto) {
    throw new Error('useAuth deve ser usado dentro de <AuthProvider>.')
  }

  return contexto
}
