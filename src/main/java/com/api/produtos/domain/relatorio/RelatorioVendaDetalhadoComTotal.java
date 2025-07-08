package com.api.produtos.domain.relatorio;

import java.math.BigDecimal;
import java.util.List;

public class RelatorioVendaDetalhadoComTotal {
    
    private List<RelatorioVendaDia> vendas;
    private BigDecimal valorTotal;
    private int quantidadeVendas;
    private int quantidadeItens;

    public RelatorioVendaDetalhadoComTotal(List<RelatorioVendaDia> vendas) {
        this.vendas = vendas;
        this.calcularTotais();
    }

    private void calcularTotais() {
        this.quantidadeVendas = vendas.size();
        this.quantidadeItens = vendas.stream()
                .mapToInt(venda -> venda.getItens().size())
                .sum();
        
        this.valorTotal = vendas.stream()
                .flatMap(venda -> venda.getItens().stream())
                .map(RelatorioVendaDiaItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // Getters
    public List<RelatorioVendaDia> getVendas() {
        return vendas;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public int getQuantidadeVendas() {
        return quantidadeVendas;
    }

    public int getQuantidadeItens() {
        return quantidadeItens;
    }

    // Setters
    public void setVendas(List<RelatorioVendaDia> vendas) {
        this.vendas = vendas;
        this.calcularTotais();
    }
} 