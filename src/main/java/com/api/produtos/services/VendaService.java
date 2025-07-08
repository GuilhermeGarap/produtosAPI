package com.api.produtos.services;

import java.time.LocalDateTime;
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
    public Venda adicionarProdutoNaVenda(Long vendaId, String codigoBarras) {
        logger.info("Adicionando produto {} na venda {}", codigoBarras, vendaId);
        
        Venda venda = vendaRepo.findById(vendaId)
                .orElseThrow(() -> new VendaNotFoundException(vendaId));

        Produto produto = produtoRepo.findByCodigoBarras(codigoBarras)
                .orElseThrow(() -> new ProdutoNotFoundException(codigoBarras, "código de barras"));

        if (produto.getDisponivelEmEstoque() <= 0) {
            logger.warn("Tentativa de adicionar produto sem estoque: produto={}, estoque={}", 
                       produto.getNomeDoProduto(), produto.getDisponivelEmEstoque());
            throw new EstoqueInsuficienteException(produto.getNomeDoProduto(), 
                                                  produto.getDisponivelEmEstoque(), 1);
        }

        Optional<VendaProduto> itemExistente = itemRepo.findByVendaAndProduto(venda, produto);

        if (itemExistente.isPresent()) {
            VendaProduto item = itemExistente.get();
            item.setQuantidade(item.getQuantidade() + 1);
            itemRepo.save(item);
            logger.info("Quantidade do produto {} aumentada na venda {}", 
                       produto.getNomeDoProduto(), vendaId);
        } else {
            VendaProduto novo = new VendaProduto();
            novo.setVenda(venda);
            novo.setProduto(produto);
            novo.setQuantidade(1);
            novo.setPrecoUnitario(produto.getPrecoVenda());
            itemRepo.save(novo);
            logger.info("Produto {} adicionado pela primeira vez na venda {}", 
                       produto.getNomeDoProduto(), vendaId);
        }

        produto.setDisponivelEmEstoque(produto.getDisponivelEmEstoque() - 1);
        produtoRepo.save(produto);
        logger.info("Estoque do produto {} reduzido para {}", 
                   produto.getNomeDoProduto(), produto.getDisponivelEmEstoque());

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

        // Apagar os itens da venda (por cascade, deve apagar tudo)
        vendaRepo.delete(venda);
        logger.info("Venda {} cancelada e estoque ajustado", id);
    }
}
