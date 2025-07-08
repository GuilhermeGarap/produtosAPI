package com.api.produtos.exceptions;

public class CodigoBarrasDuplicadoException extends RuntimeException {
    
    public CodigoBarrasDuplicadoException(String codigoBarras) {
        super("Já existe um produto cadastrado com o código de barras: " + codigoBarras);
    }
} 