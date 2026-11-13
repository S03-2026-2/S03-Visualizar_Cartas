# Frontend — Visualizar Cartas

SPA em React + TypeScript + Vite que consome o backend Spring Boot do projeto.

## Pré-requisitos

- Node.js 22+ (testado com Node 24)
- Backend rodando (por padrão em `http://localhost:8080`)

## Comandos

```bash
npm install       # instala as dependências
npm run dev       # servidor de desenvolvimento em http://localhost:5173
npm run build     # checagem de tipos + build de produção em dist/
npm run preview   # serve localmente o build de produção
npm run lint      # análise estática (Oxlint)
```

## Proxy do backend

O `vite.config.ts` encaminha `/autenticacao` e `/cartas` para `http://localhost:8080`. Para usar
outra porta:

```bash
VITE_API_TARGET=http://localhost:18080 npm run dev
```

## Estrutura

```
src/
  api/        types.ts (contrato do backend) e client.ts (fetch + ApiError)
  auth/       AuthContext.tsx — sessão em localStorage (chave vc.user.v1)
  components/ CardTile, CardToolbar, CollectionSummary, SpriteCarousel, States
  hooks/      useRequest.ts (requisição com cancelamento/retry) e useHolo.ts
  pages/      LoginPage, CardsPage, CardDetailPage
  styles/     tokens.css, global.css e módulos CSS por tela
```

## Comportamento

- **Login**: `POST /autenticacao/login` com `application/x-www-form-urlencoded`. A resposta (`User`)
  fica em `localStorage`; não há token nem sessão no servidor.
- **Coleção**: `GET /cartas?usuarioId=...&ordenar=true`. Busca, filtro por tipo e ordenação ficam
  na URL (`?busca=...&tipos=...&ordem=...`) e são aplicados no cliente.
- **Detalhe**: `GET /cartas/{id}`. Os sprites navegam dentro do próprio card, em carrossel circular
  (botões e setas do teclado).
- **Estados**: carregamento, erro com "Tentar novamente", coleção vazia e filtro sem resultado são
  estados distintos.

## Limitações conhecidas

- As credenciais de demonstração não aparecem na interface; ficam em
  `../visualizar_cartas/src/main/resources/mocks/usuarios.json`.
- Sprites e cries vêm do espelho da PokéAPI no GitHub e dependem de acesso à internet.
- O cry usa Ogg: em navegadores sem suporte (Safari) o botão informa que o áudio não está
  disponível.
- Sem token ou expiração de sessão — o logout apenas remove o usuário do `localStorage`.
