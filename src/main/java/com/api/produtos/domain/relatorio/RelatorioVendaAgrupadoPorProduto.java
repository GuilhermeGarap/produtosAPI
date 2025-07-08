package com.api.produtos.domain.relatorio;

import java.math.BigDecimal;

public class RelatorioVendaAgrupadoPorProduto {

    private String nomeDoProduto;
    private Long quantidade;
    private BigDecimal valorUnitario;
    private BigDecimal subtotal;

    // Construtor que a query do repositório espera
    public RelatorioVendaAgrupadoPorProduto(String nomeDoProduto, Long quantidade, BigDecimal valorUnitario, BigDecimal subtotal) {
        this.nomeDoProduto = nomeDoProduto;
        this.quantidade = quantidade;
        this.valorUnitario = valorUnitario;
        this.subtotal = subtotal;
    }

    // Getters e setters

    public String getNomeDoProduto() {
        return nomeDoProduto;
    }

    public void setNomeDoProduto(String nomeDoProduto) {
        this.nomeDoProduto = nomeDoProduto;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
} 