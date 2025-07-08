package com.api.produtos.domain.produto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

public record DadosCadastroProduto(

@NotBlank(message = "É necessário prover o nome do produto")
String nomeDoProduto,
@NotBlank(message = "É necessário prover um código de barras")
String codigoBarras,
@PositiveOrZero(message = "O preço de venda não pode ser negativo")
@NotNull(message = "É necessário prover um preço de venda")
BigDecimal precoVenda,
@PositiveOrZero(message = "O preço de custo não pode ser negativo")
BigDecimal precoCusto,
@PositiveOrZero(message = "A quantidade em estoque não pode ser negativa")
Integer disponivelEmEstoque

) {}
