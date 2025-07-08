package com.api.produtos.exceptions;

public class ProdutoNotFoundException extends RuntimeException {
    
    public ProdutoNotFoundException(String message) {
        super(message);
    }
    
    public ProdutoNotFoundException(Long id) {
        super("Produto com ID " + id + " não encontrado");
    }
    
    public ProdutoNotFoundException(String codigoBarras, String tipo) {
        super("Produto com " + tipo + " " + codigoBarras + " não encontrado");
    }
} 