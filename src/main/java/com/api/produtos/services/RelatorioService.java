package com.api.produtos.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.api.produtos.domain.relatorio.RelatorioVendaAgrupadoPorProduto;
import com.api.produtos.domain.relatorio.RelatorioVendaDia;
import com.api.produtos.domain.relatorio.RelatorioVendaDiaItemDTO;
import com.api.produtos.domain.venda.Venda;
import com.api.produtos.domain.venda.VendaRepository;

@Service
public class RelatorioService {

    private final VendaRepository vendaRepository;

    public RelatorioService(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }

    public List<RelatorioVendaDia> gerarRelatorioVendasDetalhadoPorPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        LocalDateTime inicio = dataInicio.atStartOfDay();
        LocalDateTime fim = dataFim.atTime(LocalTime.MAX);

        List<Venda> vendas = vendaRepository.buscarVendasComItensPorPeriodo(inicio, fim);

        return vendas.stream().map(v -> {
            List<RelatorioVendaDiaItemDTO> itensDTO = v.getItens().stream().map(item -> new RelatorioVendaDiaItemDTO(
                    item.getProduto().getNomeDoProduto(),
                    item.getQuantidade(),
                    item.getPrecoUnitario()
            )).collect(Collectors.toList());

            return new RelatorioVendaDia(
                    v.getId(),
                    v.getDataHora(),
                    v.getMetodoPagamento(),
                    itensDTO
            );
        }).collect(Collectors.toList());
    }

    public List<RelatorioVendaDia> gerarRelatorioVendasDetalhadoDoDia(LocalDate data) {
        return gerarRelatorioVendasDetalhadoPorPeriodo(data, data);
    }

    public List<RelatorioVendaAgrupadoPorProduto> gerarRelatorioVendasAgrupadoDoDia(LocalDate data) {
        return vendaRepository.findRelatorioVendasAgrupadoPorData(data);
    }
}
