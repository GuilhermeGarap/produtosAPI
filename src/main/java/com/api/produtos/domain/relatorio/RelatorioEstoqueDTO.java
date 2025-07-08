package com.api.produtos.domain.relatorio;

public record RelatorioEstoqueDTO(
    String nomeDoProduto,
    String codigoBarras,
    Integer quantidadeEstoque
) {}

