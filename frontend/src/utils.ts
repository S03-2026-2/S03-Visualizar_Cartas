import type { Carta, Sprite } from './api/types'

export type Ordem = 'nome-asc' | 'nome-desc' | 'exp-desc' | 'exp-asc' | 'original'

const TYPE_LABELS: Record<string, string> = {
  fire: 'Fogo',
  water: 'Água',
  grass: 'Planta',
  electric: 'Elétrico',
  psychic: 'Psíquico',
  ice: 'Gelo',
  dragon: 'Dragão',
  dark: 'Sombrio',
  fairy: 'Fada',
  normal: 'Normal',
  fighting: 'Lutador',
  flying: 'Voador',
  poison: 'Venenoso',
  ground: 'Terrestre',
  rock: 'Pedra',
  bug: 'Inseto',
  ghost: 'Fantasma',
  steel: 'Aço',
}

function capitalize(value: string): string {
  if (!value) {
    return ''
  }

  return value.charAt(0).toLocaleUpperCase('pt-BR') + value.slice(1).toLocaleLowerCase('pt-BR')
}

/** "mr-mime" -> "Mr Mime" */
export function formatNome(nome: string): string {
  return nome
    .split(/[-_\s]+/)
    .filter(Boolean)
    .map(capitalize)
    .join(' ')
}

function formatMedida(valor: number, sufixo: string): string {
  if (!Number.isFinite(valor)) {
    return `0,0${sufixo}`
  }

  return `${(valor / 10).toFixed(1).replace('.', ',')}${sufixo}`
}

/** Decímetros -> metros, uma casa decimal com vírgula. */
export function formatAltura(altura: number): string {
  return formatMedida(altura, ' m')
}

/** Hectogramas -> quilogramas, uma casa decimal com vírgula. */
export function formatPeso(peso: number): string {
  return formatMedida(peso, ' kg')
}

/** Rótulo em português para o tipo canônico da API; desconhecido -> capitalizado. */
export function typeLabel(tipo: string): string {
  return TYPE_LABELS[tipo.toLowerCase()] ?? capitalize(tipo)
}

function isOfficialArtwork(sprite: Sprite): boolean {
  return sprite.nome.includes('official-artwork') && sprite.nome.endsWith('front_default')
}

/** Artwork oficial -> front_default -> primeiro sprite -> null. */
export function artworkOf(carta: Carta): Sprite | null {
  const sprites = carta.sprites ?? []

  const official = sprites.find(isOfficialArtwork)
  if (official) {
    return official
  }

  const front = sprites.find((sprite) => sprite.nome.endsWith('front_default'))
  if (front) {
    return front
  }

  return sprites[0] ?? null
}

function spriteRank(sprite: Sprite): number {
  if (isOfficialArtwork(sprite)) {
    return 0
  }

  const nome = sprite.nome.toLowerCase()

  if (nome.includes('home')) {
    return 1
  }

  if (nome.includes('showdown')) {
    return 2
  }

  if (nome.includes('front')) {
    return 3
  }

  if (nome.includes('back')) {
    return 4
  }

  return 5
}

/**
 * Remove sprites de jogos antigos ("versions"), URLs vazias e duplicatas.
 * Ordena por relevância (artwork, home, showdown, front, back) e usa a lista
 * completa como fallback caso o filtro não deixe nenhum sprite.
 */
export function selectSprites(carta: Carta): Sprite[] {
  const seen = new Set<string>()
  const validos: Sprite[] = []

  for (const sprite of carta.sprites ?? []) {
    const url = sprite.url?.trim()

    if (!url || seen.has(url)) {
      continue
    }

    seen.add(url)
    validos.push({ ...sprite, url })
  }

  const filtrados = validos.filter(
    (sprite) => !sprite.nome.toLowerCase().includes('versions'),
  )

  const base = filtrados.length > 0 ? filtrados : validos

  return [...base].sort((a, b) => spriteRank(a) - spriteRank(b))
}

/** Retorna uma nova lista ordenada conforme a opção escolhida. */
export function sortCards(cards: readonly Carta[], ordem: Ordem): Carta[] {
  const copia = [...cards]

  switch (ordem) {
    case 'nome-asc':
      return copia.sort((a, b) => a.nome.localeCompare(b.nome, 'pt-BR'))
    case 'nome-desc':
      return copia.sort((a, b) => b.nome.localeCompare(a.nome, 'pt-BR'))
    case 'exp-desc':
      return copia.sort(
        (a, b) => b.experience - a.experience || a.nome.localeCompare(b.nome, 'pt-BR'),
      )
    case 'exp-asc':
      return copia.sort(
        (a, b) => a.experience - b.experience || a.nome.localeCompare(b.nome, 'pt-BR'),
      )
    case 'original':
    default:
      return copia
  }
}

function normalize(value: string): string {
  return value
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLocaleLowerCase('pt-BR')
    .trim()
}

/**
 * Busca sem acento/caixa pelo nome e filtra por tipos canônicos com lógica OU.
 */
export function filterCards(
  cards: readonly Carta[],
  busca: string,
  tipos: readonly string[] = [],
): Carta[] {
  const termo = normalize(busca)
  const selecionados = new Set(tipos.map((tipo) => tipo.toLowerCase()))

  return cards.filter((carta) => {
    const combinaBusca = !termo || normalize(carta.nome).includes(termo)
    if (!combinaBusca) {
      return false
    }

    if (selecionados.size === 0) {
      return true
    }

    return carta.tipos.some((tipo) => selecionados.has(tipo.nome.toLowerCase()))
  })
}

export type Resumo = {
  total: number
  /** Nomes canônicos dos tipos presentes, sem repetição e em ordem alfabética. */
  tipos: string[]
  experienciaMedia: number
}

export function summarize(cards: readonly Carta[]): Resumo {
  const nomes = new Set<string>()
  let somaExperiencia = 0

  for (const carta of cards) {
    somaExperiencia += carta.experience
    for (const tipo of carta.tipos) {
      nomes.add(tipo.nome)
    }
  }

  const total = cards.length

  return {
    total,
    tipos: [...nomes].sort((a, b) => a.localeCompare(b, 'pt-BR')),
    experienciaMedia: total === 0 ? 0 : Math.round(somaExperiencia / total),
  }
}
