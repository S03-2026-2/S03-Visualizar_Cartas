# Frontend — Visualizar Cartas

SPA em React + TypeScript + Vite que consome o backend Spring Boot do projeto.

## Pré-requisitos

- Node.js 20.19+ ou 22.12+ (testado com Node 24)
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

### Container com nginx

No Docker Compose, o container do frontend usa nginx e encaminha as requisições de `/api` para o
serviço `backend`. O fallback de SPA (`try_files`) cobre as rotas `/login`, `/cartas` e
`/cartas/:id`, que são devolvidas pelo `index.html` da aplicação.

### Desenvolvimento local

O `vite.config.ts` encaminha `/api` para `http://localhost:8080`. Para usar outro endereço do
backend, defina `VITE_API_TARGET`:

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

- **Login**: `POST /api/autenticacao/login` com `application/x-www-form-urlencoded`. A resposta (`User`)
  fica em `localStorage`; não há token nem sessão no servidor.
- **Coleção**: `GET /api/cartas?usuarioId=...&ordenar=true`. Busca, filtro por tipo e ordenação ficam
  na URL (`?busca=...&tipos=...&ordem=...`) e são aplicados no cliente.
- **Detalhe**: `GET /api/cartas/{id}`. Os sprites navegam dentro do próprio card, em carrossel circular
  (botões e setas do teclado).
- **Estados**: carregamento, erro com "Tentar novamente", coleção vazia e filtro sem resultado são
  estados distintos.

## Limitações conhecidas

- As credenciais de demonstração não aparecem na interface; ficam em
  `../visualizar_cartas/src/main/resources/mocks/usuarios.json`.
- Os dados das cartas — e, portanto, a coleção — vêm do espelho da PokéAPI no GitHub. Sem acesso a
  `raw.githubusercontent.com`, o login funciona, mas a coleção não carrega. Sprites e cries também
  dependem desse acesso externo.
- O cry usa Ogg: em navegadores sem suporte (Safari) o botão informa que o áudio não está
  disponível.
- Sem token ou expiração de sessão — o logout apenas remove o usuário do `localStorage`.
