# REST API — Testes com JUnit e Spring Boot

API REST para gerenciamento de mensagens, desenvolvida como projeto de estudos focado em estratégias de teste com JUnit 5, Mockito e testes de integração com Spring Boot.

Projeto desenvolvido durante a **Pós Graduação em Arquitetura e Desenvolvimento Java** da **FIAP**.

---

## Tecnologias

| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 3.1.1 |
| JUnit 5 | 5.9.3 |
| Mockito | (via spring-boot-starter-test) |
| AssertJ | 3.24.2 |
| Rest-Assured | 5.3.0 |
| H2 (banco em memória) | (via spring-boot) |

---

## Endpoints

Base URL: `http://localhost:8080`

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/mensagens` | Registra uma nova mensagem |
| `GET` | `/mensagens/{id}` | Busca uma mensagem pelo ID |

### Exemplo de payload (POST /mensagens)

```json
{
  "usuario": "Rafael",
  "conteudo": "Minha primeira mensagem"
}
```

---

## Estratégia de Testes

O projeto cobre três camadas de testes:

### Testes Unitários (`*Test.java`)
Testam cada camada de forma isolada, substituindo dependências por mocks via Mockito. Rápidos e sem necessidade de infraestrutura.

- `MensagemServiceTest` — lógica do service com repositório mockado
- `MensagemControllerTest` — comportamento do controller com service mockado
- `MensagemRepositoryTest` — queries do repositório

### Testes de Integração (`*IT.java`)
Sobem o contexto completo do Spring Boot com banco H2 em memória, verificando que as camadas funcionam em conjunto.

- `MensagemServiceIT` — fluxo completo service + repositório + banco

---

## Como executar

### Pré-requisitos
- Java 21
- Maven 3.8+

### Rodar a aplicação

```bash
mvn spring-boot:run
```

### Rodar todos os testes

```bash
mvn test
```

### Rodar apenas testes de integração

```bash
mvn test -Dtest="*IT"
```
