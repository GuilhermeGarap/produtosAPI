package com.api.produtos.exceptions;

public class ProdutoNaoPodeSerExcluidoException extends RuntimeException {
    public ProdutoNaoPodeSerExcluidoException(Long id) {
        super("O produto com ID " + id + " não pode ser excluído pois está vinculado a outras entidades ou ocorreu um erro de integridade.");
    }
} 