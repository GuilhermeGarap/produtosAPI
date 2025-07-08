package com.api.produtos.domain.venda;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.produtos.domain.relatorio.RelatorioVendaAgrupado;
import com.api.produtos.domain.relatorio.RelatorioVendaAgrupadoPorProduto;

public interface VendaRepository extends JpaRepository<Venda, Long> {
    @Query("""
        SELECT new com.api.produtos.domain.relatorio.RelatorioVendaAgrupado(
            p.nomeDoProduto,
            cast(v.dataHora as date),
            v.metodoPagamento,
            SUM(vp.quantidade)
        )
        FROM Venda v
        JOIN v.itens vp
        JOIN vp.produto p
        WHERE v.dataHora BETWEEN :inicio AND :fim
        GROUP BY p.nomeDoProduto, cast(v.dataHora as date), v.metodoPagamento
        ORDER BY p.nomeDoProduto, cast(v.dataHora as date)
    """)
    List<RelatorioVendaAgrupado> gerarRelatorioVendasAgrupado(LocalDateTime inicio, LocalDateTime fim);

    @EntityGraph(attributePaths = {"itens", "itens.produto"})
    @Query("SELECT v FROM Venda v WHERE v.dataHora BETWEEN :inicio AND :fim ORDER BY v.dataHora")
    List<Venda> buscarVendasComItensPorPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);

    @Query("""
        SELECT new com.api.produtos.domain.relatorio.RelatorioVendaAgrupadoPorProduto(
            p.nomeDoProduto,
            SUM(vp.quantidade),
            vp.precoUnitario,
            SUM(vp.quantidade * vp.precoUnitario)
        )
        FROM Venda v
        JOIN v.itens vp
        JOIN vp.produto p
        WHERE FUNCTION('DATE', v.dataHora) = :data
        GROUP BY p.nomeDoProduto, vp.precoUnitario
    """)
    List<RelatorioVendaAgrupadoPorProduto> findRelatorioVendasAgrupadoPorData(@Param("data") LocalDate data);





}
