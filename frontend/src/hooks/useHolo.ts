import { useCallback, type PointerEvent as ReactPointerEvent } from 'react'

/**
 * Devolve um handler de `pointermove` que posiciona o brilho holográfico
 * conforme o ponteiro. As variáveis `--mx`/`--my` são escritas no próprio
 * elemento para serem consumidas pelo `holo.module.css`.
 */
export function useHolo<T extends HTMLElement = HTMLElement>() {
  return useCallback((event: ReactPointerEvent<T>) => {
    if (event.pointerType !== 'mouse') {
      return
    }

    const { currentTarget } = event
    const rect = currentTarget.getBoundingClientRect()

    if (rect.width === 0 || rect.height === 0) {
      return
    }

    const x = ((event.clientX - rect.left) / rect.width) * 100
    const y = ((event.clientY - rect.top) / rect.height) * 100

    currentTarget.style.setProperty('--mx', `${x.toFixed(2)}%`)
    currentTarget.style.setProperty('--my', `${y.toFixed(2)}%`)
  }, [])
}
