# FStats API

![Java](https://img.shields.io/badge/Java-17+-red) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.x-Light_Green) ![Docker](https://img.shields.io/badge/Docker-blue) ![Redis](https://img.shields.io/badge/Redis-red)

API desenvolvida para análise estatística de partidas de futebol com base em dados históricos de ligas. O sistema avalia gols marcados e sofridos, além do desempenho das equipes em casa e fora, para estimar padrões e faixas prováveis de gols utilizando modelagem estatística baseada na distribuição de Poisson.

---
## Tecnologias

-  Java 17+
-  Spring Boot
-  Spring Security + JWT
-  MySQL
-  Redis
-  JPA / Hibernate
-  Flyway Migration
- Swagger / OpenAPI
- Docker

---
## Como executar

Este projeto utiliza Docker para facilitar a execução do ambiente.

**1 - Clonar o repositório**

```bash
git clone https://github.com/LeviPereira9/fstats.git
cd fstats
```

**2 - Configure as variáveis de ambiente**

Com base no arquivo `.env.example`, crie um `.env` preenchendo as variáveis e/ou configure diretamente nos arquivos `.properties` para execução local.

**3 - Execute a aplicação**

```bash
docker-compose up -d
```

A aplicação estará disponível em:

[http://localhost:8080](http://localhost:8080)

---

## Arquitetura

A aplicação segue uma arquitetura em camadas:

- **Config** - Configurações da aplicação.
- **Controller** - Exposição da API REST.
- **DTO** - Objetos de transferência de dados.
- **Exception** - Exceções customizadas e tratamento de erros.
- **Integration** - Integração com APIs externas.
- **Model** - Entidades de domínio.
- **Repository** - Acesso e persistência de dados.
- **Response** - Padronização das respostas da API.
- **Security** - Autenticação, autorização e filtros de segurança.
- **Service** - Regras de negócio.
- **Util** - Classes utilitárias reutilizáveis.

---
## Segurança

A API utiliza autenticação baseada em **JWT**, com controle de acesso por roles.

- Endpoints públicos e protegidos
- Tokens JWT enviados via header `Authorization`
- Controle de permissões com Spring Security

A documentação completa dos endpoints protegidos está disponível no Swagger.

---
## Cache

O Redis é utilizado para cache de dados frequentemente acessados, reduzindo a carga no banco de dados e melhorando o tempo de resposta da API.

---
## Documentação da API

A documentação completa dos endpoints, parâmetros e exemplos de requisição está disponível via Swagger:

- **Local:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
- **Produção:** [https://fstats.onrender.com/swagger-ui/index.html](https://fstats.onrender.com/swagger-ui/index.html)

> ⚠ A API em produção pode entrar em modo *sleep*. Nesse caso, aguarde alguns minutos até que o serviço seja reativado.

---

## Padrão de Respostas

A API utiliza um formato padronizado para respostas de sucesso e erro, independentemente do endpoint acessado.

### Estrutura da resposta

- `operation` - Operação executada pela API
- `code` - Código HTTP da resposta.
- `message` - Mensagem descritiva do resultado da operação.
- `data` -  Conteúdo retornado em caso de sucesso.
- `timestamp` - Data e hora da resposta.
- `fieldErrors` - Campos inválidos ou erros de validação (quando aplicável).
- `actions` - Ações disponíveis relacionadas à resposta (quando aplicável).

> ⚠ Campos opcionais podem não estar presentes dependendo do tipo de resposta.

#### Exemplo de sucesso:

```JSON
{
	"operation": "Auth.Register",
	"code": 201,
	"message": "Usuário cadastrado com sucesso.",
	"data": {
		"token": "Bearer Lorem"
	},
	"timestamp": "2025-01-10T14:32:18"
}
```

#### Exemplo de erro:

```json
{
  "operation": "Auth.Register",
  "code": 400,
  "message": "Erro de validação",
  "timestamp": "2025-01-10T14:32:18",
  "fieldErrors": {
    "username": "Informe o nome de usuário.",
    "email": "Informe um e-mail válido.",
    "password": "A senha precisa conter no mínimo 8 caracteres."
  }
}
```

---
