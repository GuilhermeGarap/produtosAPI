package com.api.produtos.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.produtos.domain.venda.Venda;
import com.api.produtos.domain.venda.Venda.MetodoPagamento;
import com.api.produtos.services.VendaService;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/vendas")
public class VendaController {
    
    @Autowired
    private VendaService vendaService;

    @PostMapping("/criarVenda")
    public ResponseEntity<Venda> criarVenda() {
        Venda venda = vendaService.criarVenda();
        return ResponseEntity.ok(venda);
    }

    @GetMapping("/buscarVenda/{id}")
    public ResponseEntity<Venda> buscar(@PathVariable Long id) {
        Venda venda = vendaService.buscarVenda(id);
        return ResponseEntity.ok(venda);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Venda>> listarTodasVendas() {
        List<Venda> vendas = vendaService.listarTodasVendas();
        return ResponseEntity.ok(vendas);
    }

    @PostMapping("/adicionarProdutoVenda/{id}")
    public ResponseEntity<Venda> adicionarProduto(
            @PathVariable Long id, 
            @RequestParam @NotBlank(message = "Código do produto é obrigatório") String codigo,
            @RequestParam(defaultValue = "1") int quantidade) {
        Venda atualizada = vendaService.adicionarProdutoNaVenda(id, codigo, quantidade);
        return ResponseEntity.ok(atualizada);
    }

    @PostMapping("/finalizarVenda/{id}")
    public ResponseEntity<Venda> finalizarVenda(
            @PathVariable Long id, 
            @RequestParam @NotNull(message = "Método de pagamento é obrigatório") MetodoPagamento metodo) {
        Venda venda = vendaService.buscarVenda(id);
        venda.setMetodoPagamento(metodo);
        venda.setFinalizada(true);
        vendaService.salvarVenda(venda);
        return ResponseEntity.ok(venda);
    }

    @DeleteMapping("/cancelarVenda/{id}")
    public ResponseEntity<String> cancelarVenda(@PathVariable Long id) {
        vendaService.cancelarVenda(id);
        return ResponseEntity.ok("Venda cancelada e estoque ajustado.");
    }

    @DeleteMapping("/removerProdutoVenda/{id}")
    public ResponseEntity<Venda> removerProduto(
            @PathVariable Long id, 
            @RequestParam @NotBlank(message = "Código do produto é obrigatório") String codigo) {
        Venda atualizada = vendaService.removerProdutoDaVenda(id, codigo);
        return ResponseEntity.ok(atualizada);
    }

    @GetMapping("/vendasHoje")
    public ResponseEntity<List<Venda>> vendasHoje() {
        List<Venda> vendas = vendaService.buscarVendasHoje();
        return ResponseEntity.ok(vendas);
    }
}
