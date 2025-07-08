package com.api.produtos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProdutoRequestDTO {
    
    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 2, max = 100, message = "Nome deve ter entre 2 e 100 caracteres")
    private String nomeDoProduto;
    
    @NotBlank(message = "Código de barras é obrigatório")
    @Size(min = 8, max = 20, message = "Código de barras deve ter entre 8 e 20 caracteres")
    private String codigoBarras;
    
    @NotNull(message = "Preço de venda é obrigatório")
    @Positive(message = "Preço de venda deve ser positivo")
    private Double precoVenda;
    
    @NotNull(message = "Preço de custo é obrigatório")
    @Positive(message = "Preço de custo deve ser positivo")
    private Double precoCusto;
    
    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Positive(message = "Quantidade em estoque deve ser positiva")
    private Integer disponivelEmEstoque;
    
    // Getters e Setters
    public String getNomeDoProduto() {
        return nomeDoProduto;
    }
    
    public void setNomeDoProduto(String nomeDoProduto) {
        this.nomeDoProduto = nomeDoProduto;
    }
    
    public String getCodigoBarras() {
        return codigoBarras;
    }
    
    public void setCodigoBarras(String codigoBarras) {
        this.codigoBarras = codigoBarras;
    }
    
    public Double getPrecoVenda() {
        return precoVenda;
    }
    
    public void setPrecoVenda(Double precoVenda) {
        this.precoVenda = precoVenda;
    }
    
    public Double getPrecoCusto() {
        return precoCusto;
    }
    
    public void setPrecoCusto(Double precoCusto) {
        this.precoCusto = precoCusto;
    }
    
    public Integer getDisponivelEmEstoque() {
        return disponivelEmEstoque;
    }
    
    public void setDisponivelEmEstoque(Integer disponivelEmEstoque) {
        this.disponivelEmEstoque = disponivelEmEstoque;
    }
} 