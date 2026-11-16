# S03-Visualizar_Cartas

Projeto da Matéria de Arquitetura de Software — aplicação **Visualização de Cartas**.

O sistema exibe a coleção de cartas de cada jogador. A autenticação e a distribuição de cartas são
simuladas a partir de arquivos JSON e os dados dos Pokémons são obtidos no espelho da PokéAPI
publicado pelo repositório oficial da PokeAPI no GitHub.

## Arquitetura

| Camada | Tecnologia |
| --- | --- |
| Backend | Java 25, Spring Boot 4.1.1, Maven (`visualizar_cartas/`) |
| Frontend | React 19, TypeScript, Vite, React Router, CSS Modules (`frontend/`) |

O backend expõe três endpoints:

| Método | Rota | Descrição |
| --- | --- | --- |
| `POST` | `/api/autenticacao/login` | Recebe `email` e `senha` (form-urlencoded) e devolve o usuário autenticado (`401` quando inválido) |
| `GET` | `/api/cartas?usuarioId={id}&ordenar=true` | Devolve a distribuição de cartas do usuário |
| `GET` | `/api/cartas/{id}` | Devolve o detalhe de uma carta |

O profile `mock` (padrão) usa os adapters JSON locais e é o caminho suportado. A integração real
ainda não está disponível: os contratos dos serviços de jogadores e de distribuição de cartas não
existem, e o profile `real` ainda não fornece todos os beans necessários para iniciar o backend.

## Dados de demonstração

- `visualizar_cartas/src/main/resources/mocks/usuarios.json` — jogadores de demonstração
  (e-mail, senha, id e ranking). **As credenciais de demonstração ficam somente neste arquivo.**
- `visualizar_cartas/src/main/resources/mocks/distribuicao.json` — cartas (ids da PokéAPI) sorteadas
  para cada jogador.

Nenhum dado de login é exibido na interface.

## Como executar com Docker

A porta `8080` do host precisa estar livre. O Docker Compose sobe o backend sem publicar uma porta
no host e encaminha as chamadas da API pelo container do frontend.

```bash
docker compose up --build      # sobe backend + frontend
# aplicação em http://localhost:8080
docker compose logs -f backend
docker compose down
```

Se a porta `8080` já estiver ocupada, publique o frontend em outra porta:

```bash
PORTA_FRONTEND=18080 docker compose up --build
# aplicação em http://localhost:18080
```

## Execução manual

### 1. Backend

Requer Docker. O projeto usa Java 25, então a build é feita dentro de um container Maven:

```bash
cd visualizar_cartas
docker run --rm -p 8080:8080 \
  -v "$PWD":/app -w /app -v "$HOME/.m2":/root/.m2 \
  maven:3.9-eclipse-temurin-25 mvn -q spring-boot:run
```

O backend sobe em `http://localhost:8080` já com o profile `mock` (padrão definido em
`application.properties`). Para gerar o pacote:

```bash
cd visualizar_cartas
docker run --rm -v "$PWD":/app -w /app -v "$HOME/.m2":/root/.m2 \
  maven:3.9-eclipse-temurin-25 mvn -q -DskipTests package
```

A integração real ainda não está disponível: os contratos dos serviços de jogadores e de distribuição
não existem. Portanto, não existe um comando suportado para iniciar o backend com o profile `real`;
use o profile `mock` acima.

### 2. Frontend

Requer Node.js 20.19+ ou 22.12+:

```bash
cd frontend
npm install
npm run dev
```

A SPA sobe em `http://localhost:5173` e o Vite faz proxy de `/api` para `http://localhost:8080`.
Para apontar para outro endereço do backend:

```bash
VITE_API_TARGET=http://localhost:18080 npm run dev
```

### 3. Build de produção do frontend

```bash
cd frontend
npm run build   # gera frontend/dist
npm run preview # serve o build localmente
```

## Scripts do frontend

| Script | O que faz |
| --- | --- |
| `npm run dev` | Servidor de desenvolvimento com proxy para o backend |
| `npm run build` | Checagem de tipos (`tsc -b`) + build de produção |
| `npm run lint` | Análise estática com Oxlint |
| `npm run preview` | Serve o build de produção |

## Stack e restrições do frontend

- React + TypeScript + Vite + React Router, requisições com `fetch` e estilos com CSS Modules.
- Sem TanStack Query, Zustand, Tailwind, bibliotecas de UI ou fontes via CDN: as fontes são
  empacotadas com `@fontsource`.
- Sem token de autenticação: o usuário autenticado fica em `localStorage` (chave `vc.user.v1`) e o
  `usuarioId` viaja na requisição da coleção.

## Estrutura

```
visualizar_cartas/   backend Spring Boot (controllers + adapters + profiles)
  src/main/java/br/com/pokemon/adapters/   PokeAPIAdapter (real) e adapters de demonstração (mock)
  src/main/resources/mocks/               usuários e distribuição de demonstração
frontend/           SPA React + TypeScript
  src/api/          tipos e cliente HTTP
  src/auth/         contexto de autenticação e sessão
  src/components/   cards, toolbar, carrossel de sprites e estados
  src/pages/        login, coleção e detalhe da carta
  src/styles/       tokens, layout e estilos por módulo
```
