package com.api.produtos.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class VendaCleanupScheduler {
    private final VendaService vendaService;

    public VendaCleanupScheduler(VendaService vendaService) {
        this.vendaService = vendaService;
    }

    // Executa a cada minuto
    @Scheduled(fixedRate = 60000)
    public void limparVendasVaziasAntigas() {
        int removidas = vendaService.deletarVendasVaziasAntigas();
        if (removidas > 0) {
            System.out.println("Vendas vazias removidas automaticamente: " + removidas);
        }
    }
} 