package com.api.produtos.exceptions;

public class EstoqueInsuficienteException extends RuntimeException {
    
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
    
    public EstoqueInsuficienteException(String produto, int quantidadeDisponivel, int quantidadeSolicitada) {
        super("Estoque insuficiente para o produto " + produto + 
              ". Disponível: " + quantidadeDisponivel + 
              ", Solicitado: " + quantidadeSolicitada);
    }
} 