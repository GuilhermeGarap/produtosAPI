package com.api.produtos.domain.produto;

import java.math.BigDecimal;

public record DadosDetalhamentoProduto(

Long id,
String nomeDoProduto,
String codigoBarras,
BigDecimal precoCusto,
BigDecimal precoVenda,
Integer disponivelEmEstoque

) {
    public DadosDetalhamentoProduto(Produto produto) {
        this(produto.getId(), produto.getNomeDoProduto(), produto.getCodigoBarras(), produto.getPrecoCusto(), produto.getPrecoVenda(), produto.getDisponivelEmEstoque());
    }
}
