import { useEffect, useState, type KeyboardEvent } from 'react'
import type { Sprite } from '../api/types'
import styles from '../styles/detail.module.css'

type SpriteCarouselProps = {
  sprites: Sprite[]
  alt: string
}

function indiceInicial(sprites: Sprite[]): number {
  return sprites.findIndex(
    (sprite) =>
      sprite.nome.includes('official-artwork') &&
      sprite.nome.endsWith('front_default'),
  )
}

export default function SpriteCarousel({ sprites, alt }: SpriteCarouselProps) {
  const total = sprites.length
  const [index, setIndex] = useState(() => Math.max(indiceInicial(sprites), 0))
  const [totalAnterior, setTotalAnterior] = useState(total)

  // A lista pode mudar (ex.: navegação entre cartas). Fora do intervalo -> 0.
  // Ajuste durante o render evita um frame com índice inválido.
  if (totalAnterior !== total) {
    setTotalAnterior(total)
    setIndex(index >= total ? 0 : index)
  }

  // Pré-carrega o próximo e o anterior.
  useEffect(() => {
    if (sprites.length < 2) {
      return
    }

    const vizinhos = [
      sprites[(index + 1) % sprites.length],
      sprites[(index - 1 + sprites.length) % sprites.length],
    ]

    for (const vizinho of vizinhos) {
      if (vizinho) {
        const imagem = new Image()
        imagem.src = vizinho.url
      }
    }
  }, [index, sprites])

  const safeIndex = index >= total ? 0 : index
  const atual: Sprite | undefined = sprites[safeIndex] ?? sprites[0]

  function mover(delta: number) {
    if (total < 2) {
      return
    }

    setIndex((atualIndice) => {
      const base = atualIndice >= total ? 0 : atualIndice
      return (base + delta + total) % total
    })
  }

  function handleKeyDown(event: KeyboardEvent<HTMLDivElement>) {
    if (event.key === 'ArrowLeft') {
      event.preventDefault()
      mover(-1)
    } else if (event.key === 'ArrowRight') {
      event.preventDefault()
      mover(1)
    }
  }

  return (
    <div
      className={styles.carousel}
      role="group"
      tabIndex={0}
      aria-roledescription="carrossel"
      aria-label={`Sprites de ${alt}`}
      onKeyDown={handleKeyDown}
    >
      {atual ? (
        <img
          className={styles.spriteImage}
          src={atual.url}
          alt={alt}
          width={475}
          height={475}
          loading={safeIndex === 0 ? 'eager' : 'lazy'}
          decoding="async"
        />
      ) : (
        <div className={styles.spriteEmpty} role="img" aria-label={alt}>
          <span aria-hidden="true">{alt.charAt(0) || '?'}</span>
        </div>
      )}

      {total > 1 && (
        <>
          <button
            className={`${styles.arrow} ${styles.arrowPrev}`}
            type="button"
            aria-label="Sprite anterior"
            onClick={() => mover(-1)}
          >
            <span aria-hidden="true">‹</span>
          </button>
          <button
            className={`${styles.arrow} ${styles.arrowNext}`}
            type="button"
            aria-label="Próximo sprite"
            onClick={() => mover(1)}
          >
            <span aria-hidden="true">›</span>
          </button>
        </>
      )}

      {total > 0 && (
        <div className={styles.spriteMeta}>
          <span className={styles.spriteCounter} aria-live="polite">
            {safeIndex + 1} / {total}
          </span>
          {atual && (
            <span className={styles.spriteName} aria-hidden="true">
              {atual.nome}
            </span>
          )}
        </div>
      )}
    </div>
  )
}
