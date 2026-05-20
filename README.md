# SmartCar — Precificação Inteligente de Veículos Usados

> Projeto desenvolvido para a disciplina de **Projeto Integrador I**

API REST que analisa anúncios de veículos usados cruzando preço FIPE, quilometragem e estado de conservação, classificando o negócio e gerando uma explicação humanizada via IA (Groq).

---

## Tecnologias

| Camada | Stack |
|--------|-------|
| Linguagem & Runtime | Java 17 + Spring Boot 3.2 |
| Segurança | Spring Security + JWT (JJWT 0.12) |
| Persistência | Spring Data JPA + MySQL + Flyway |
| Integrações | OpenFeign · Groq API |
| Utilitários | Lombok |

---

## Modelagem (UML)

![Diagrama UML do SmartCar](assets/SmartCarUML.png)

---

## Estrutura do Projeto

```
src/main/java/com/glc/smartcar/
│
├── config/
│   ├── JwtAuthenticationFilter     # Intercepta requisições e valida o token JWT
│   ├── SecurityConfig              # Configuração do Spring Security e beans de auth
│   └── TokenProvider               # Geração e validação de tokens JWT
│
├── core/
│   ├── auth/
│   │   ├── AuthController          # POST /auth/cadastrar e /auth/login
│   │   ├── AuthService             # Lógica de cadastro e login
│   │   └── dto/
│   │       ├── AuthRegisterRequest
│   │       ├── AuthRegisterResponse
│   │       ├── AuthLoginRequest
│   │       └── AuthLoginResponse
│   │
│   ├── avaliacoes/
│   │   ├── AvaliacoesController    # POST /sc, PATCH /sc/{id}, GET /sc
│   │   ├── AvaliacoesService       # Orquestra FIPE + classificação + IA
│   │   ├── AvaliacoesMapper        # Conversão entre entidade e DTOs
│   │   ├── AvaliacoesRepository    # Queries JPA
│   │   ├── ClassificacaoService    # Calcula preço justo e classifica o negócio
│   │   ├── Avaliacoes              # Entidade JPA
│   │   └── dto/
│   │       ├── AvaliacaoRequestDTO
│   │       └── AvaliacaoResponseDTO
│   │
│   └── user/
│       ├── Usuario                 # Entidade JPA + UserDetails
│       ├── UserDetailsImpl         # Carrega usuário pelo email
│       ├── UserRepository          # findByEmail, existsByEmail
│       ├── UsuarioMapper
│       ├── UsuarioService
│       └── UserRoleEnum            # USER, ADMIN
│
└── integration/
    ├── fipe/
    │   ├── FipeController
    │   ├── FipeAdapter
    │   ├── FipeClient
    │   └── port/FipePort
    │
    └── ia/
        ├── IaAdapter
        ├── IaClient
        └── port/IaPort
```

---

## Endpoints

### Auth — público

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/auth/cadastrar` | Cadastra novo usuário |
| POST | `/auth/login` | Retorna token JWT |

### Avaliações — requer token

| Método | Rota | Descrição |
|--------|------|-----------|
| POST | `/sc` | Cria avaliação de veículo |
| GET | `/sc` | Lista avaliações ativas do usuário |
| PATCH | `/sc/{id}` | Desativa avaliação (soft delete) |

### FIPE — requer token

| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/fipe` | Lista marcas |
| GET | `/fipe/marcas/{brandId}/modelos` | Lista modelos da marca |
| GET | `/fipe/marcas/{brandId}/modelos/{modelId}/anos` | Lista anos do modelo |
| GET | `/fipe/marcas/{brandId}/modelos/{modelId}/anos/{yearId}` | Retorna preço FIPE |

---

## Lógica de Classificação

O **preço justo** é calculado a partir do preço FIPE ajustado por dois fatores:

- **Quilometragem:** decaimento exponencial — acima de 14.000 km/ano penaliza, abaixo bonifica (intervalo: −40% a +15%)
- **Conservação:** impacto moderado no preço (intervalo: −20% a +20%)

| Status | Critério |
|--------|----------|
| `OTIMO_NEGOCIO` | Preço anunciado ≤ 90% do preço justo |
| `NA_MEDIA` | 90% — 105% |
| `ACIMA_DA_MEDIA` | 105% — 120% |
| `DIFICIL_DE_VENDER` | > 120% |

---

## Como Executar

### Pré-requisitos

- Java 17+
- MySQL rodando localmente
- Chave da [Groq API](https://console.groq.com)

### Variáveis de ambiente

| Variável | Descrição |
|----------|-----------|
| `JWT_KEY` | Chave secreta para assinar os tokens JWT |
| `JWT_EXPIRATION` | Expiração em ms — ex.: `86400000` (24 h) |
| `API_KEY` | Chave da Groq API |
| `SEU_USUARIO` | Usuário do banco MySQL |
| `SUA_SENHA` | Senha do banco MySQL |

### Rodando

```bash
# Clone o repositório
git clone https://github.com/gusthavomnz/smart-car-api.git
cd smart-car-api

# Configure as variáveis de ambiente e execute
./mvnw spring-boot:run
```

O Flyway aplicará as migrations automaticamente ao subir a aplicação.
