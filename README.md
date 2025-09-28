# Desafio Itaú Unibanco - API de Transações e Estatísticas

Este projeto implementa uma **API REST** em **Java** com **Spring Boot** para gerenciar transações e calcular estatísticas em tempo real, seguindo as diretrizes do desafio de programação do Itaú Unibanco.

**Restrição Crítica:** Todos os dados são armazenados **em memória**, conforme a exigência do desafio, sem a utilização de bancos de dados ou sistemas de cache externos.

-----

## 1\. Tecnologias e Arquitetura

O projeto utiliza a seguinte arquitetura para cumprir as restrições técnicas:

  * **Linguagem / Framework:** Java 17+ / Spring Boot.
  * **Armazenamento:** Em Memória (`ConcurrentHashMap`), garantindo concorrência segura.
  * **Valor (`valor`):** Utiliza `java.math.BigDecimal` para garantir precisão monetária.
  * **Data/Hora (`dataHora`):** Utiliza `java.time.OffsetDateTime` para aderir ao padrão **ISO 8601**.
  * **Estatísticas:** Implementadas com `DoubleSummaryStatistics` para agregação eficiente.
  * **Configuração (Extra):** O tempo de cálculo é configurável via propriedade `transacao.janela-segundos` (padrão 60s).

-----

## 2\. Endpoints da API

A API expõe três *endpoints* principais, manipulando exclusivamente objetos **JSON**.

### 2.1. POST /transacao - Receber Transação

Endpoint responsável por receber e validar novas transações.

**Corpo da Requisição (JSON):**

  * **`valor`**: Valor em decimal com ponto flutuante (Ex: `123.45`).
  * **`dataHora`**: Data/Hora no padrão ISO 8601 (Ex: `"2020-08-07T12:34:56.789-03:00"`).

**Códigos de Resposta:**

  * **`201 Created`**: Transação válida, aceita e registrada.
  * **`422 Unprocessable Entity`**: Transação rejeitada (Regra: valor $\textless 0$ ou data futura).
  * **`400 Bad Request`**: A API não compreendeu a requisição (ex: JSON inválido).

### 2.2. DELETE /transacao - Limpar Transações

Apaga **todos** os dados de transações armazenados em memória.

**Código de Resposta:**

  * **`200 OK`**: Todas as transações foram removidas com sucesso.

### 2.3. GET /estatistica - Calcular Estatísticas

Retorna estatísticas sobre transações que ocorreram na **janela de tempo configurada** (padrão 60 segundos).

**Corpo da Resposta (JSON):**

  * **`count`**: Quantidade de transações no período.
  * **`sum`**: Soma total do valor transacionado.
  * **`avg`**: Média do valor transacionado.
  * **`min`**: Menor valor transacionado.
  * **`max`**: Maior valor transacionado.

**Nota:** Se não houver transações na janela de tempo, todos os campos são retornados com o valor **`0`** (zero).

-----

## 3\. Como Executar o Projeto

### Pré-requisitos

  * Java Development Kit (JDK) 17 ou superior.
  * Maven.

### Passos de Execução

1.  **Clonar o Repositório:**
2.  **Construir o Projeto:** `./mvnw clean install`
3.  **Executar o JAR:** `java -jar target/*.jar`

A aplicação estará rodando em `http://localhost:8080`.

### Configuração da Janela de Tempo

Para alterar o tempo de 60 segundos para o cálculo das estatísticas, adicione ou modifique a propriedade no seu arquivo `application.properties`:

```properties
# Exemplo para uma janela de 120 segundos
transacao.janela-segundos=120 
```
