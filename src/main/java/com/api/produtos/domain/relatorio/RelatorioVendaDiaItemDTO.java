package com.api.produtos.domain.relatorio;

import java.math.BigDecimal;

public class RelatorioVendaDiaItemDTO {
    private String nomeDoProduto;
    private int quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal subtotal;

    public RelatorioVendaDiaItemDTO(String nomeDoProduto, int quantidade, BigDecimal valorUnitario) {
        this.nomeDoProduto = nomeDoProduto;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.subtotal = valorUnitario.multiply(new java.math.BigDecimal(quantidade));
    }

    // getters e setters
    public String getNomeDoProduto() { return nomeDoProduto; }
    public void setNomeDoProduto(String nomeDoProduto) { this.nomeDoProduto = nomeDoProduto; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
    public BigDecimal getValorUnitario() { return valorUnitario; }
    public void setValorUnitario(BigDecimal valorUnitario) { this.valorUnitario = valorUnitario; }
    public BigDecimal getSubtotal() { return subtotal; }
}
