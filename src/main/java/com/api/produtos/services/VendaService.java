package com.api.produtos.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.api.produtos.domain.produto.Produto;
import com.api.produtos.domain.produto.ProdutoRepository;
import com.api.produtos.domain.venda.Venda;
import com.api.produtos.domain.venda.VendaRepository;
import com.api.produtos.domain.vendaproduto.VendaProduto;
import com.api.produtos.domain.vendaproduto.VendaProdutoRepository;
import com.api.produtos.exceptions.EstoqueInsuficienteException;
import com.api.produtos.exceptions.ProdutoNotFoundException;
import com.api.produtos.exceptions.VendaNotFoundException;

@Service
public class VendaService {
    
    private static final Logger logger = LoggerFactory.getLogger(VendaService.class);
    
    @Autowired
    private VendaRepository vendaRepo;

    @Autowired
    private ProdutoRepository produtoRepo;

    @Autowired
    private VendaProdutoRepository itemRepo;

    @Transactional
    public Venda criarVenda() {
        logger.info("Criando nova venda");
        Venda venda = new Venda();
        venda.setDataHora(LocalDateTime.now());
        Venda vendaCriada = vendaRepo.save(venda);
        logger.info("Venda criada com ID: {}", vendaCriada.getId());
        return vendaCriada;
    }

    @Transactional
    public Venda adicionarProdutoNaVenda(Long vendaId, String codigoBarras, int quantidade) {
        logger.info("Adicionando produto {} na venda {} com quantidade {}", codigoBarras, vendaId, quantidade);
        
        Venda venda = vendaRepo.findById(vendaId)
                .orElseThrow(() -> new VendaNotFoundException(vendaId));

        Produto produto = produtoRepo.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new ProdutoNotFoundException(codigoBarras, "código de barras"));

        if (produto.getDisponivelEmEstoque() < quantidade) {
            logger.warn("Tentativa de adicionar produto com estoque insuficiente: produto={}, estoque={}, quantidade={}", 
                       produto.getNomeDoProduto(), produto.getDisponivelEmEstoque(), quantidade);
            throw new EstoqueInsuficienteException(produto.getNomeDoProduto(), 
                                                  produto.getDisponivelEmEstoque(), quantidade);
        }

        Optional<VendaProduto> itemExistente = itemRepo.findByVendaAndProduto(venda, produto);

        if (itemExistente.isPresent()) {
            VendaProduto item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + quantidade);
            itemRepo.save(item);
            logger.info("Quantidade do produto {} aumentada na venda {} em {} unidades", 
                       produto.getNomeDoProduto(), vendaId, quantidade);
        } else {
            VendaProduto novo = new VendaProduto();
            novo.setVenda(venda);
            novo.setProduto(produto);
            novo.setQuantidade(quantidade);
            novo.setPrecoUnitario(produto.getPrecoVenda());
            itemRepo.save(novo);
            logger.info("Produto {} adicionado pela primeira vez na venda {} com {} unidades", 
                       produto.getNomeDoProduto(), vendaId, quantidade);
        }

        produto.setDisponivelEmEstoque(produto.getDisponivelEmEstoque() - quantidade);
        produtoRepo.save(produto);
        logger.info("Estoque do produto {} reduzido para {}", 
                   produto.getNomeDoProduto(), produto.getDisponivelEmEstoque());

        return vendaRepo.findById(vendaId).get();
    }

    @Transactional
    public Venda removerProdutoDaVenda(Long vendaId, String codigoBarras) {
        logger.info("Removendo produto {} da venda {}", codigoBarras, vendaId);
        
        Venda venda = vendaRepo.findById(vendaId)
                .orElseThrow(() -> new VendaNotFoundException(vendaId));

        Produto produto = produtoRepo.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new ProdutoNotFoundException(codigoBarras, "código de barras"));

        Optional<VendaProduto> itemExistente = itemRepo.findByVendaAndProduto(venda, produto);

        if (itemExistente.isPresent()) {
            VendaProduto item = itemExistente.get();
            
            if (item.getQuantidade() > 1) {
                // Diminuir quantidade
                item.setQuantidade(item.getQuantidade() - 1);
                itemRepo.save(item);
                logger.info("Quantidade do produto {} diminuída na venda {}", 
                           produto.getNomeDoProduto(), vendaId);
            } else {
                // Remover item completamente
                itemRepo.delete(item);
                logger.info("Produto {} removido completamente da venda {}", 
                           produto.getNomeDoProduto(), vendaId);
            }

            // Repor estoque
            produto.setDisponivelEmEstoque(produto.getDisponivelEmEstoque() + 1);
            produtoRepo.save(produto);
            logger.info("Estoque do produto {} reposto para {}", 
                       produto.getNomeDoProduto(), produto.getDisponivelEmEstoque());
        } else {
            logger.warn("Tentativa de remover produto inexistente na venda: produto={}, venda={}", 
                       produto.getNomeDoProduto(), vendaId);
            throw new RuntimeException("Produto não encontrado na venda");
        }

        return vendaRepo.findById(vendaId).get();
    }

    public Venda buscarVenda(Long id) {
        logger.info("Buscando venda com ID: {}", id);
        Venda venda = vendaRepo.findById(id)
                .orElseThrow(() -> new VendaNotFoundException(id));
        logger.info("Venda encontrada: ID={}, Data={}, Itens={}", 
                   id, venda.getDataHora(), venda.getItens().size());
        return venda;
    }

    @Transactional
    public Venda salvarVenda(Venda venda) {
        logger.info("Salvando venda com ID: {}", venda.getId());
        Venda vendaSalva = vendaRepo.save(venda);
        logger.info("Venda salva com sucesso: ID={}", vendaSalva.getId());
        return vendaSalva;
    }

    @Transactional
    public void cancelarVenda(Long id) {
        logger.info("Cancelando venda com ID: {}", id);
        
        Venda venda = vendaRepo.findById(id)
                .orElseThrow(() -> new VendaNotFoundException(id));

        // Repor estoque dos produtos vendidos
        for (VendaProduto item : venda.getItens()) {
            Produto produto = item.getProduto();
            int estoqueAnterior = produto.getDisponivelEmEstoque();
            produto.setDisponivelEmEstoque(produto.getDisponivelEmEstoque() + item.getQuantidade());
            produtoRepo.save(produto);
            logger.info("Estoque do produto {} reposto: {} -> {}", 
                       produto.getNomeDoProduto(), estoqueAnterior, produto.getDisponivelEmEstoque());
        }

        // Apaga os itens da venda
        vendaRepo.delete(venda);
        logger.info("Venda {} cancelada e estoque ajustado", id);
    }

    public List<Venda> listarTodasVendas() {
        logger.info("Listando todas as vendas");
        return vendaRepo.findAll();
    }

    public List<Venda> buscarVendasHoje() {
        LocalDateTime hoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime amanha = hoje.plusDays(1);
        logger.info("Buscando vendas de hoje: {} até {}", hoje, amanha);
        return vendaRepo.findByDataHoraBetweenAndFinalizadaTrue(hoje, amanha);
    }

    @Transactional
    public int deletarVendasVaziasAntigas() {
        LocalDateTime limite = LocalDateTime.now().minusMinutes(10);
        List<Venda> vendasAntigas = vendaRepo.findAll().stream()
            .filter(v -> (v.getItens() == null || v.getItens().isEmpty()) && v.getDataHora().isBefore(limite))
            .toList();
        vendasAntigas.forEach(vendaRepo::delete);
        return vendasAntigas.size();
    }
}
