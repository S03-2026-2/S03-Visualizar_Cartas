import { useCallback, useEffect, useRef, useState } from 'react'
import { ApiError } from '../api/client'

export type RequestStatus = 'idle' | 'loading' | 'success' | 'error'

export type RequestState<T> = {
  data: T | null
  error: Error | null
  status: RequestStatus
  refetch: () => void
}

const RETRY_DELAY_MS = 600

/** Erro de rede (status 0) ou falha do servidor (5xx) merece uma nova tentativa. */
function isRetryable(error: unknown): boolean {
  return error instanceof ApiError && (error.status === 0 || error.status >= 500)
}

function toError(value: unknown): Error {
  if (value instanceof Error) {
    return value
  }

  return new Error('Não foi possível carregar os dados.')
}

/** Espera cancelável: resolve `false` quando o signal é abortado. */
function wait(ms: number, signal: AbortSignal): Promise<boolean> {
  return new Promise((resolve) => {
    if (signal.aborted) {
      resolve(false)
      return
    }

    const timer = window.setTimeout(() => {
      signal.removeEventListener('abort', onAbort)
      resolve(true)
    }, ms)

    function onAbort() {
      window.clearTimeout(timer)
      resolve(false)
    }

    signal.addEventListener('abort', onAbort, { once: true })
  })
}

/**
 * Requisição com cancelamento, descarte de respostas obsoletas e um retry
 * automático para falhas transitórias (rede ou 5xx).
 *
 * `key` identifica a requisição; `fn` é lida via ref para que uma função
 * instável não reinicie a busca a cada render.
 */
export function useRequest<T>(
  key: string,
  fn: (signal: AbortSignal) => Promise<T>,
): RequestState<T> {
  const [data, setData] = useState<T | null>(null)
  const [error, setError] = useState<Error | null>(null)
  const [status, setStatus] = useState<RequestStatus>('idle')
  const [attempt, setAttempt] = useState(0)

  const fnRef = useRef(fn)

  useEffect(() => {
    fnRef.current = fn
  }, [fn])

  useEffect(() => {
    const controller = new AbortController()
    let active = true

    async function run() {
      setStatus('loading')
      setError(null)

      let lastError: unknown = null

      for (let tentativa = 0; tentativa < 2; tentativa += 1) {
        try {
          const resultado = await fnRef.current(controller.signal)

          if (!active) {
            return
          }

          setData(resultado)
          setStatus('success')
          return
        } catch (falha) {
          if (!active || controller.signal.aborted) {
            return
          }

          lastError = falha

          if (tentativa > 0 || !isRetryable(falha)) {
            break
          }

          const continuar = await wait(RETRY_DELAY_MS, controller.signal)

          if (!continuar || !active) {
            return
          }
        }
      }

      if (!active) {
        return
      }

      setData(null)
      setError(toError(lastError))
      setStatus('error')
    }

    void run()

    return () => {
      active = false
      controller.abort()
    }
  }, [key, attempt])

  const refetch = useCallback(() => {
    setError(null)
    setStatus('loading')
    setAttempt((valor) => valor + 1)
  }, [])

  return { data, error, status, refetch }
}
