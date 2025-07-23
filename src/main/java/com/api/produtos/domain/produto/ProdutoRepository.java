package com.api.produtos.domain.produto;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.api.produtos.domain.relatorio.RelatorioEstoqueDTO;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    Optional<Produto> findByCodigoBarras(String codigoBarras);

    // Buscar apenas produtos ativos (não removidos)
    @Query("SELECT p FROM Produto p WHERE p.codigoBarras NOT LIKE 'removido_%'")
    List<Produto> findProdutosAtivos();

@Query("""
    SELECT new com.api.produtos.domain.relatorio.RelatorioEstoqueDTO(
        p.nomeDoProduto,
        p.codigoBarras,
        p.disponivelEmEstoque
    )
    FROM Produto p
    WHERE p.codigoBarras NOT LIKE 'removido_%'
""")
List<RelatorioEstoqueDTO> gerarRelatorioEstoque();

}
