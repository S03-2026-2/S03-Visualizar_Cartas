import { Link } from 'react-router-dom'
import type { Carta } from '../api/types'
import { useHolo } from '../hooks/useHolo'
import {
  artworkOf,
  formatAltura,
  formatNome,
  formatPeso,
  typeLabel,
} from '../utils'
import styles from '../styles/cards.module.css'
import holo from '../styles/holo.module.css'

type CardTileProps = {
  carta: Carta
  index: number
}

export default function CardTile({ carta, index }: CardTileProps) {
  const nome = formatNome(carta.nome)
  const artwork = artworkOf(carta)
  const tipos = carta.tipos ?? []
  const tipoPrimario = tipos[0]?.nome
  const onPointerMove = useHolo<HTMLAnchorElement>()

  const experiencia = Number.isFinite(carta.experience)
    ? `EXP ${carta.experience}`
    : null
  const altura = Number.isFinite(carta.altura) ? formatAltura(carta.altura) : null
  const peso = Number.isFinite(carta.peso) ? formatPeso(carta.peso) : null

  return (
    <Link
      className={`${styles.card} ${holo.holo}`}
      data-tipo={tipoPrimario}
      to={`/cartas/${carta.id}`}
      aria-label={`Ver detalhes de ${nome}`}
      onPointerMove={onPointerMove}
    >
      <div className={styles.cardTop}>
        <span className={styles.cardNumber}>
          #{String(carta.id).padStart(3, '0')}
        </span>

        <span className={styles.cardTypes}>
          {tipos.map((tipo) => (
            <span
              key={`${tipo.id}-${tipo.nome}`}
              className={styles.chip}
              data-tipo={tipo.nome}
            >
              {typeLabel(tipo.nome)}
            </span>
          ))}
        </span>
      </div>

      <div className={styles.cardArt}>
        {artwork ? (
          <img
            src={artwork.url}
            alt={`Arte de ${nome}`}
            width={320}
            height={320}
            loading={index === 0 ? 'eager' : 'lazy'}
            decoding="async"
          />
        ) : (
          <div className={styles.silhouette} role="img" aria-label={nome}>
            <span aria-hidden="true">{nome.charAt(0) || '?'}</span>
          </div>
        )}
      </div>

      <div className={styles.cardBody}>
        <h2 className={styles.cardName}>{nome}</h2>

        {(experiencia || altura || peso) && (
          <span className={styles.cardStats}>
            {experiencia && <span>{experiencia}</span>}
            {altura && <span>{altura}</span>}
            {peso && <span>{peso}</span>}
          </span>
        )}

        <span className={styles.cardCta} aria-hidden="true">
          Ver detalhes →
        </span>
      </div>
    </Link>
  )
}
