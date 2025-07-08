package com.api.produtos.domain.relatorio;

import java.time.LocalDate;

import com.api.produtos.domain.venda.Venda.MetodoPagamento;

public class RelatorioVendaAgrupado {
    private String nomeDoProduto;
    private LocalDate dataHora;
    private MetodoPagamento metodoPagamento;
    private Long quantidadeTotal;

public RelatorioVendaAgrupado(String nomeDoProduto, java.sql.Date dataSql, MetodoPagamento metodoPagamento, Long quantidadeTotal) {
    this.nomeDoProduto = nomeDoProduto;
    this.dataHora = dataSql.toLocalDate();
    this.metodoPagamento = metodoPagamento;
    this.quantidadeTotal = quantidadeTotal;
}


    public String getNomeDoProduto() {
        return nomeDoProduto;
    }

    public void setNomeDoProduto(String nomeDoProduto) {
        this.nomeDoProduto = nomeDoProduto;
    }

    public LocalDate getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDate dataHora) {
        this.dataHora = dataHora;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public Long getQuantidadeTotal() {
        return quantidadeTotal;
    }

    public void setQuantidadeTotal(Long quantidadeTotal) {
        this.quantidadeTotal = quantidadeTotal;
    }
}
