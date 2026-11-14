export type Tipo = {
  id: number
  nome: string
  url: string
}

export type Habilidade = {
  id: number
  nome: string
  geracao: string
}

export type Sprite = {
  nome: string
  url: string
}

export type Carta = {
  id: number
  experience: number
  nome: string
  urlCry: string
  /** Altura em decímetros. */
  altura: number
  /** Peso em hectogramas. */
  peso: number
  habilidades: Habilidade[]
  tipos: Tipo[]
  sprites: Sprite[]
}

export type Usuario = {
  id: number
  nome: string
  ranking: number
  cartas?: Carta[]
}
