# 🚘💸 SmartCar - Uma Solução inteligente para Precificação de Veículos

###  Projeto desenvolvido para a matéria de Projeto Integrador I



## 🏗️ Modelagem do Sistema (UML)
### Abaixo está o diagrama de classes que representa a estrutura do banco de dados e as relações entre as entidades:
![Diagrama UML do SmartCar](assets/SmartCarUML.png)



# SmartCar API

Guia inteligente para avaliação e venda de veículos usados. A API analisa anúncios com base na tabela FIPE, quilometragem e conservação do veículo, classificando negócios e gerando uma explicação humanizada via IA.

---

## Tecnologias

- Java 17 + Spring Boot 3.2
- Spring Security + JWT (JJWT 0.12)
- Spring Data JPA + MySQL
- Flyway
- OpenFeign
- Groq API (IA)
- Lombok

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
│   │   ├── dto/
│   │   │   ├── AvaliacaoRequestDTO
│   │   │   └── AvaliacaoResponseDTO
│   │   └── enums/
│   │       ├── Conservacao         # NOVO, BOM, REGULAR, RUIM
│   │       ├── HistoricoAtivo      # SIM, NAO (soft delete)
│   │       └── StatusResultado     # OTIMO_NEGOCIO, NA_MEDIA, ACIMA_DA_MEDIA, DIFICIL_DE_VENDER
│   │
│   └── user/
│       ├── Usuario                 # Entidade JPA + implementação de UserDetails
│       ├── UserDetailsImpl         # Carrega usuário pelo email para o Spring Security
│       ├── UserRepository          # findByEmail, existsByEmail
│       ├── UsuarioMapper           # Conversão entre entidade e DTOs
│       ├── UsuarioService          # Service de operações do usuário
│       └── UserRoleEnum            # USER, ADMIN
│
└── integration/
    ├── fipe/
    │   ├── FipeController          # GET /fipe, /fipe/marcas/{id}/modelos, etc.
    │   ├── FipeAdapter             # Implementação do FipePort via Feign
    │   ├── FipeClient              # Feign client para a API da FIPE
    │   ├── port/
    │   │   └── FipePort            # Interface (porta de saída)
    │   └── dto/
    │       ├── FipeNameAndCode
    │       └── FipeVeiculoDTO
    │
    └── ia/
        ├── IaAdapter               # Implementação do IaPort — monta e envia prompt
        ├── IaClient                # Feign client para a Groq API
        ├── port/
        │   └── IaPort              # Interface (porta de saída)
        └── dto/
            ├── Message
            ├── iaRequest
            ├── iaResponse
            └── Choice
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
| POST | `/sc` | Cria uma avaliação de veículo |
| GET | `/sc` | Lista avaliações ativas do usuário |
| PATCH | `/sc/{id}` | Desativa uma avaliação (soft delete) |

### FIPE — requer token
| Método | Rota | Descrição |
|--------|------|-----------|
| GET | `/fipe` | Lista marcas |
| GET | `/fipe/marcas/{brandId}/modelos` | Lista modelos da marca |
| GET | `/fipe/marcas/{brandId}/modelos/{modelId}/anos` | Lista anos do modelo |
| GET | `/fipe/marcas/{brandId}/modelos/{modelId}/anos/{yearId}` | Retorna preço FIPE |

---

## Lógica de Classificação

O preço justo é calculado com base no preço FIPE ajustado por dois fatores:

- **Quilometragem:** decaimento exponencial — veículos acima da média de 14.000 km/ano são penalizados, abaixo recebem bônus (limite: -40% a +15%)
- **Conservação:** escala de impacto moderado sobre o preço (limite: -20% a +20%)

| Classificação | Critério |
|---------------|----------|
| OTIMO_NEGOCIO | Preço anunciado ≤ 90% do preço justo |
| NA_MEDIA | 90% a 105% |
| ACIMA_DA_MEDIA | 105% a 120% |
| DIFICIL_DE_VENDER | Acima de 120% |

---

## Variáveis de Ambiente

| Variável | Descrição |
|----------|-----------|
| `JWT_KEY` | Chave secreta para assinar os tokens JWT |
| `JWT_EXPIRATION` | Tempo de expiração do token em ms (ex: `86400000` para 24h) |
| `API_KEY` | Chave da Groq API |
| `SEU_USUARIO` | Usuário do banco MySQL |
| `SUA_SENHA` | Senha do banco MySQL |
