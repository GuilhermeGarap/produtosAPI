package com.api.produtos.domain.relatorio;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.api.produtos.domain.venda.Venda.MetodoPagamento;

public class RelatorioVendaDia {

    private Long idVenda;
    private LocalDateTime dataHora;
    private MetodoPagamento metodoPagamento;
    private List<RelatorioVendaDiaItemDTO> itens;

    // Construtor para relatório detalhado
    public RelatorioVendaDia(Long idVenda, LocalDateTime dataHora, MetodoPagamento metodoPagamento, List<RelatorioVendaDiaItemDTO> itens) {
        this.idVenda = idVenda;
        this.dataHora = dataHora;
        this.metodoPagamento = metodoPagamento;
        this.itens = itens;
    }

    // Construtor que a query do repositório espera (mantido para compatibilidade)
    public RelatorioVendaDia(String nomeDoProduto, Long quantidade, BigDecimal valorUnitario, BigDecimal subtotal) {
        // Este construtor não é usado no relatório detalhado
        throw new UnsupportedOperationException("Use o construtor para relatório detalhado");
    }

    // Getters e setters

    public Long getIdVenda() {
        return idVenda;
    }

    public void setIdVenda(Long idVenda) {
        this.idVenda = idVenda;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(MetodoPagamento metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public List<RelatorioVendaDiaItemDTO> getItens() {
        return itens;
    }

    public void setItens(List<RelatorioVendaDiaItemDTO> itens) {
        this.itens = itens;
    }
}
