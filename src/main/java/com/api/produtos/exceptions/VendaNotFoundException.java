package com.api.produtos.exceptions;

public class VendaNotFoundException extends RuntimeException {
    
    public VendaNotFoundException(String message) {
        super(message);
    }
    
    public VendaNotFoundException(Long id) {
        super("Venda com ID " + id + " não encontrada");
    }
} 