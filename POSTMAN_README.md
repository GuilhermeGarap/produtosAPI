# 📋 Guia de Uso - Coleção Postman

Este guia explica como usar a coleção do Postman para testar a API do Sistema de Produtos e Vendas.

## 🚀 Como Importar

### 1. Importar Coleção
1. Abra o Postman
2. Clique em **Import**
3. Selecione o arquivo `Sistema_Produtos_Vendas.postman_collection.json`
4. Clique em **Import**

### 2. Importar Ambiente
1. Clique em **Import** novamente
2. Selecione o arquivo `Sistema_Produtos_Vendas.postman_environment.json`
3. Clique em **Import**
4. Selecione o ambiente **"Sistema de Produtos - Ambiente Local"** no dropdown superior direito

## 🔧 Configuração

### Variáveis de Ambiente
A coleção usa as seguintes variáveis:

| Variável | Valor Padrão | Descrição |
|----------|---------------|-----------|
| `base_url` | `http://localhost:8080` | URL base da API |
| `produto_id` | `1` | ID de exemplo para produtos |
| `venda_id` | `1` | ID de exemplo para vendas |
| `codigo_barras` | `7891234567890` | Código de barras de exemplo |
| `data_inicio` | `2024-01-01` | Data inicial para relatórios |
| `data_fim` | `2024-01-31` | Data final para relatórios |

### Como Alterar Variáveis
1. Clique no ícone de engrenagem (⚙️) no canto superior direito
2. Selecione o ambiente **"Sistema de Produtos - Ambiente Local"**
3. Edite os valores conforme necessário
4. Clique em **Save**

## 📚 Estrutura da Coleção

### 🛍️ Produtos
- **Listar Produtos**: `GET /produtos/listar`
- **Cadastrar Produto**: `POST /produtos/cadastrar`
- **Atualizar Estoque**: `POST /produtos/atualizarEstoque/{id}`
- **Editar Produto**: `PUT /produtos/editar/{id}`
- **Relatório de Estoque (Excel)**: `GET /produtos/estoque/excel`
- **Relatório de Estoque (PDF)**: `GET /produtos/estoque/pdf`

### 🛒 Vendas
- **Criar Venda**: `POST /vendas/criarVenda`
- **Buscar Venda**: `GET /vendas/buscarVenda/{id}`
- **Adicionar Produto**: `POST /vendas/adicionarProdutoVenda/{id}`
- **Finalizar Venda**: `POST /vendas/finalizarVenda/{id}`
- **Cancelar Venda**: `DELETE /vendas/cancelarVenda/{id}`

### 📊 Relatórios
- **Relatório Detalhado (JSON)**: `GET /relatorios/detalhado`
- **Relatório Detalhado (Excel)**: `GET /relatorios/detalhado/excel`
- **Relatório Detalhado (PDF)**: `GET /relatorios/detalhado/pdf`

## 🧪 Como Testar

### 1. Preparação
1. Certifique-se de que o servidor está rodando (`./mvnw spring-boot:run`)
2. Importe a coleção e o ambiente
3. Selecione o ambiente correto

### 2. Fluxo de Teste Recomendado

#### Passo 1: Cadastrar Produtos
1. Execute **"Cadastrar Produto"** com dados válidos
2. Anote o ID retornado
3. Atualize a variável `produto_id` com o novo ID

#### Passo 2: Criar e Gerenciar Vendas
1. Execute **"Criar Venda"**
2. Anote o ID da venda retornado
3. Atualize a variável `venda_id`
4. Execute **"Adicionar Produto na Venda"** usando o código de barras
5. Execute **"Finalizar Venda"** com método de pagamento

#### Passo 3: Testar Relatórios
1. Execute **"Relatório Detalhado (JSON)"** com datas válidas
2. Teste as exportações Excel e PDF

### 3. Testes de Erro
A coleção inclui exemplos de respostas de erro para:
- Código de barras duplicado
- Produto não encontrado
- Estoque insuficiente
- Erros de validação

## 📝 Exemplos de Uso

### Cadastrar Produto
```json
{
  "nomeDoProduto": "TV Samsung 55\" 4K",
  "codigoBarras": "7891234567892",
  "precoVenda": 3299.99,
  "precoCusto": 2800.00,
  "disponivelEmEstoque": 8
}
```

### Atualizar Estoque
```json
{
  "quantidadeAlterada": 5
}
```

### Adicionar Produto na Venda
```
URL: {{base_url}}/vendas/adicionarProdutoVenda/{{venda_id}}?codigo={{codigo_barras}}
```

## 🔍 Dicas de Uso

### 1. Usar Variáveis
- Substitua IDs fixos por variáveis: `{{produto_id}}`, `{{venda_id}}`
- Use `{{base_url}}` para facilitar mudança de ambiente

### 2. Testar Cenários de Erro
- Tente cadastrar produto com código duplicado
- Teste com dados inválidos
- Tente acessar recursos inexistentes

### 3. Verificar Logs
- Monitore o console do Spring Boot durante os testes
- Verifique os logs estruturados implementados

### 4. Exportar Resultados
- Use os endpoints de Excel/PDF para gerar relatórios
- Salve as respostas JSON para análise

## 🚨 Troubleshooting

### Erro de Conexão
- Verifique se o servidor está rodando na porta 8080
- Confirme se a variável `base_url` está correta

### Erro de CORS
- O sistema já tem CORS configurado
- Se persistir, verifique as configurações do navegador

### Erro de Validação
- Verifique se todos os campos obrigatórios estão preenchidos
- Confirme se os tipos de dados estão corretos

### Arquivos de Exportação Vazios
- Verifique se há dados no banco
- Confirme se as datas dos relatórios estão corretas

## 📞 Suporte

Para dúvidas sobre a coleção:
- Verifique a documentação da API no README principal
- Consulte os logs do servidor
- Teste os endpoints individualmente

---

**Coleção criada para o Sistema de Produtos e Vendas v1.0.0** 