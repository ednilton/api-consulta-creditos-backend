## 🔙 Backend - `api-consulta-creditos-backend`

API RESTful desenvolvida com Java 8 e Spring Boot 2.7.18, responsável por expor os endpoints para consulta de créditos constituídos (ISSQN), persistir os dados em PostgreSQL, enviar eventos via Kafka e aplicar validações e regras de negócio.

### ⚙️ Tecnologias e Bibliotecas Utilizadas

* **Java 8**
* **Spring Boot 2.7.18**

  * spring-boot-starter-web
  * spring-boot-starter-data-jpa
  * spring-boot-starter-validation
  * spring-boot-starter-actuator
* **Apache Kafka**: `spring-kafka`
* **Banco de Dados**: PostgreSQL 13 (driver v42.5.4)
* **Migrações**: Flyway
* **Documentação**: SpringDoc OpenAPI v1.7.0 (Swagger UI)
* **ORM**: Hibernate (via JPA)
* **Mapeamento**: MapStruct v1.5.5.Final
* **Lombok**: v1.18.30
* **Apache Commons Lang 3**
* **Testes**: JUnit 5, Mockito, Spring Boot Test, Testcontainers (PostgreSQL, Kafka), JaCoCo

### 🧪 Testes Automatizados

* Testes unitários com Mockito (`mockito-inline`)
* Testes de integração com Testcontainers para PostgreSQL e Kafka
* Cobertura com JaCoCo configurada no Maven (relatório em `target/site/jacoco/index.html`)

### 🔧 Plugins Maven

* `spring-boot-maven-plugin`
* `maven-compiler-plugin` com suporte a Lombok e MapStruct
* `jacoco-maven-plugin`
* `maven-surefire-plugin`

### 📁 Estrutura do Projeto

```
src/main/java/com/creditos/
├── config/            # Configurações gerais e beans
├── controller/        # Endpoints REST
├── service/           # Regras de negócio (Services)
├── repository/        # Repositórios (JPA)
├── dto/               # Data Transfer Objects
├── entity/            # Entidades JPA
├── exception/         # Tratamento de erros
├── messaging/         # Integração com Kafka

src/test/java/...       # Testes unitários e integração
Dockerfile              # Container backend
pom.xml                 # Gerenciador de dependências
```

## 📌 Observações Técnicas

* O frontend e o backend são independentes, mas integráveis via ambiente Docker
* A comunicação entre camadas é realizada por REST (HttpClient no Angular)
* Padrões de design e princípios **SOLID**, **Clean Code** e **KISS** foram respeitados em todo o projeto
* Utilização de **Dockerfiles** para cada módulo e **docker-compose** no repositório de infraestrutura

---

Deseja contribuir ou adaptar para outro cenário? Os repositórios estão públicos e bem documentados para facilitar sua análise e reutilização.

**Desenvolvido com excelência por Ednilton Curt Rauh**
📧 [edrauh@gmail.com](mailto:edrauh@gmail.com)
🔗 [LinkedIn](https://www.linkedin.com/in/ednilton-rauh-63838a47)
