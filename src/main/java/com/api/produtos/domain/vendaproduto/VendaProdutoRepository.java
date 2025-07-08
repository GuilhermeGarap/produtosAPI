package com.api.produtos.domain.vendaproduto;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.produtos.domain.produto.Produto;
import com.api.produtos.domain.venda.Venda;

public interface VendaProdutoRepository extends JpaRepository<VendaProduto, Long> {
    Optional<VendaProduto> findByVendaAndProduto(Venda venda, Produto produto);
}
