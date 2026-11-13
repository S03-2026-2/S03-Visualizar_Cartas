import type { Carta, Usuario } from './types'

const TIMEOUT_MS = 10_000

export class ApiError extends Error {
  readonly status: number

  constructor(status: number, message: string) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

/**
 * Combina o timeout padrão com o signal do chamador quando ambos existem.
 * `AbortSignal.any` não está disponível em todos os navegadores; nesse caso
 * priorizamos o cancelamento do chamador, mantendo o timeout quando não há signal.
 */
function withTimeout(signal?: AbortSignal | null): AbortSignal {
  const timeout = AbortSignal.timeout(TIMEOUT_MS)

  if (!signal) {
    return timeout
  }

  if (typeof AbortSignal.any === 'function') {
    return AbortSignal.any([signal, timeout])
  }

  return signal
}

async function request<T>(path: string, init: RequestInit = {}): Promise<T> {
  let response: Response

  try {
    response = await fetch(path, {
      ...init,
      signal: withTimeout(init.signal),
    })
  } catch (error) {
    if (error instanceof DOMException && error.name === 'AbortError') {
      // Cancelamento explícito do chamador: repassa para o consumidor ignorar.
      throw error
    }

    if (error instanceof DOMException && error.name === 'TimeoutError') {
      throw new ApiError(0, 'A requisição demorou demais. Tente novamente.')
    }

    throw new ApiError(0, 'Não foi possível conectar ao serviço. Tente novamente.')
  }

  if (!response.ok) {
    if (response.status === 401) {
      throw new ApiError(401, 'Não autorizado.')
    }

    throw new ApiError(
      response.status,
      `Não foi possível concluir a requisição (HTTP ${response.status}).`,
    )
  }

  if (response.status === 204) {
    return undefined as T
  }

  return (await response.json()) as T
}

export async function login(
  email: string,
  senha: string,
  signal?: AbortSignal,
): Promise<Usuario> {
  const body = new URLSearchParams({ email, senha })

  return request<Usuario>('/autenticacao/login', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body,
    signal,
  })
}

export async function listarCartas(
  usuarioId: number,
  signal?: AbortSignal,
): Promise<Carta[]> {
  const params = new URLSearchParams({
    usuarioId: String(usuarioId),
    ordenar: 'true',
  })

  return request<Carta[]>(`/cartas?${params.toString()}`, { signal })
}

export async function detalharCarta(
  id: number,
  signal?: AbortSignal,
): Promise<Carta> {
  return request<Carta>(`/cartas/${encodeURIComponent(String(id))}`, { signal })
}
