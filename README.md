# 🛍️ Sistema de Produtos e Vendas Doces Isabely

Sistema completo para gerenciamento de produtos, vendas e relatórios desenvolvido em Spring Boot.

## 🚀 Funcionalidades

### 📦 Gestão de Produtos
- Cadastro de produtos com código de barras
- Controle de estoque
- Edição de informações
- Relatórios de estoque (Excel/PDF)

### 🛒 Gestão de Vendas
- Criação de vendas
- Adição de produtos por código de barras
- Controle automático de estoque
- Finalização com método de pagamento
- Cancelamento com reposição de estoque

### 📊 Relatórios
- Relatórios detalhados de vendas por período
- Exportação em JSON, Excel e PDF
- Relatórios de estoque
- Totais e estatísticas

## 🛠️ Tecnologias

- **Backend**: Spring Boot 3.x
- **Banco de Dados**: H2 (persistente)
- **Validação**: Bean Validation
- **Logging**: SLF4J + Logback
- **Exportação**: Apache POI (Excel) + iText (PDF)

## 📋 Pré-requisitos

- Java 17 ou superior
- Maven 3.6+

## 🔧 Instalação e Execução

1. **Clone o repositório**
```bash
git clone <url-do-repositorio>
cd produtos
```

2. **Execute o projeto**
```bash
./mvnw spring-boot:run
```

3. **Acesse a aplicação**
- API: http://localhost:8080
- Console H2: http://localhost:8080/h2-console

## 📚 Endpoints da API

### Produtos

#### Listar Produtos
```http
GET /produtos/listar
```

#### Cadastrar Produto
```http
POST /produtos/cadastrar
Content-Type: application/json

{
  "nomeDoProduto": "Pé de Moleque Caseiro",
  "codigoBarras": "7891234567890",
  "precoVenda": 12.50,
  "precoCusto": 8.00,
  "disponivelEmEstoque": 50
}
```

#### Atualizar Estoque
```http
POST /produtos/atualizarEstoque/{id}
Content-Type: application/json

{
  "quantidadeAlterada": 5
}
```

#### Editar Produto
```http
PUT /produtos/editar/{id}
Content-Type: application/json

{
  "nomeDoProduto": "Pé de Moleque Caseiro Especial",
  "codigoBarras": "7891234567890",
  "precoVenda": 15.00,
  "precoCusto": 10.00
}
```

#### Excluir Produto
```http
DELETE /produtos/deletar/{id}
```
Remove um produto do sistema pelo seu ID.
- O produto será substituído por "Produto Removido" em todas as vendas existentes.
- Vendas históricas são preservadas com preços e quantidades originais.
- Resposta: 204 No Content (sucesso)
- Erros: 404 Produto não encontrado

#### Relatório de Estoque (Excel)
```http
GET /produtos/estoque/excel
```

#### Relatório de Estoque (PDF)
```http
GET /produtos/estoque/pdf
```

### Vendas

#### Criar Venda
```http
POST /vendas/criarVenda
```

#### Buscar Venda
```http
GET /vendas/buscarVenda/{id}
```

#### Adicionar Produto
```http
POST /vendas/adicionarProdutoVenda/{id}?codigo=7891234567890
```

#### Finalizar Venda
```http
POST /vendas/finalizarVenda/{id}?metodo=DINHEIRO
```

#### Cancelar Venda
```http
DELETE /vendas/cancelarVenda/{id}
```

#### Listar Todas as Vendas
```http
GET /vendas/listar
```

Retorna uma lista com todas as vendas cadastradas.

#### Vendas do Dia
```http
GET /vendas/vendasHoje
```

Retorna uma lista com todas as vendas realizadas hoje.

#### Remover Produto da Venda
```http
DELETE /vendas/removerProdutoVenda/{id}?codigo={codigo}
```

Remove um produto específico de uma venda.

Exemplo de resposta:
```json
[
  {
    "id": 1,
    "dataHora": "2024-07-10T14:30:00",
    "metodoPagamento": "DINHEIRO",
    "itens": [
      {
        "id": 1,
        "produto": {
          "id": 1,
          "nomeDoProduto": "Pé de Moleque Caseiro",
          "codigoBarras": "7891234567890"
        },
        "quantidade": 3,
        "precoUnitario": 12.50
      }
    ]
  }
]
```

### Relatórios

#### Relatório Detalhado (JSON)
```http
GET /relatorios/detalhado?dataInicio=2024-01-01&dataFim=2024-01-31
```

#### Relatório Detalhado do Dia (JSON)
```http
GET /relatorios/detalhado/dia?data=2024-01-15
```

#### Relatório Detalhado (Excel)
```http
GET /relatorios/detalhado/excel?dataInicio=2024-01-01&dataFim=2024-01-31
```

#### Relatório Detalhado (PDF)
```http
GET /relatorios/detalhado/pdf?dataInicio=2024-01-01&dataFim=2024-01-31
```

## 🔍 Tratamento de Erros

O sistema possui tratamento centralizado de erros com respostas padronizadas:

### Exemplos de Respostas de Erro

#### Produto não encontrado
```json
{
  "status": 404,
  "error": "Produto não encontrado",
  "message": "Produto com ID 123 não encontrado",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### Erro de validação
```json
{
  "status": 400,
  "error": "Erro de validação",
  "message": "Dados de entrada inválidos",
  "timestamp": "2024-01-15T10:30:00",
  "errors": {
    "nomeDoProduto": "Nome do produto é obrigatório",
    "precoVenda": "Preço de venda deve ser positivo"
  }
}
```

## 📝 Logs

O sistema registra logs detalhados para:
- Operações de CRUD
- Transações de vendas
- Alterações de estoque
- Erros e exceções

### Configuração de Logs
```properties
logging.level.com.api.produtos=INFO
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG
```

## 🔒 Segurança

- Validação de dados de entrada
- Tratamento de exceções centralizado
- Configuração de CORS
- Logs de auditoria

## 📊 Banco de Dados

### Console H2
- URL: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:file:./data/loja-db`
- Username: `sa`
- Password: (vazio)

### Tabelas Principais
- `produto`: Cadastro de produtos
- `venda`: Registro de vendas
- `venda_produto`: Itens das vendas


