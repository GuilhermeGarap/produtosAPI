package com.api.produtos.domain.produto;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "produtos")
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Produto {
    
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    private String nomeDoProduto;

    @Column(unique = true)
    private String codigoBarras;
    private BigDecimal precoVenda;
    private BigDecimal precoCusto;
    private Integer disponivelEmEstoque;

    public Produto(DadosCadastroProduto dados) {
        this.nomeDoProduto = dados.nomeDoProduto();
        this.codigoBarras = dados.codigoBarras();
        this.precoVenda = dados.precoVenda();
        this.precoCusto = dados.precoCusto(); 
        this.disponivelEmEstoque = dados.disponivelEmEstoque();
    }

}
