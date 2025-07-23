package com.api.produtos.services;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.api.produtos.domain.produto.Produto;
import com.api.produtos.domain.produto.ProdutoRepository;
import com.api.produtos.exceptions.CodigoBarrasDuplicadoException;
import com.api.produtos.exceptions.EstoqueInsuficienteException;
import com.api.produtos.exceptions.ProdutoNaoPodeSerExcluidoException;
import com.api.produtos.exceptions.ProdutoNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class ProdutoService {

    private static final Logger logger = LoggerFactory.getLogger(ProdutoService.class);

    @Autowired
    private ProdutoRepository produtoRepository;
    @Autowired
    private com.api.produtos.domain.vendaproduto.VendaProdutoRepository vendaProdutoRepository;

    // Buscar todos os produtos ativos
    public List<Produto> listarProdutos() {
        logger.info("Listando produtos ativos");
        List<Produto> produtos = produtoRepository.findProdutosAtivos();
        logger.info("Encontrados {} produtos ativos", produtos.size());
        return produtos;
    }

    // Cadastrar novo produto
    @Transactional
    public Produto cadastrarProduto(Produto produto) {
        logger.info("Cadastrando novo produto: {}", produto.getNomeDoProduto());
        
        // Verificar se código de barras já existe
        if (produtoRepository.findByCodigoBarras(produto.getCodigoBarras()).isPresent()) {
            logger.warn("Tentativa de cadastrar produto com código de barras duplicado: {}", produto.getCodigoBarras());
            throw new CodigoBarrasDuplicadoException(produto.getCodigoBarras());
        }
        
        Produto produtoSalvo = produtoRepository.save(produto);
        logger.info("Produto cadastrado com sucesso: ID={}, Nome={}", produtoSalvo.getId(), produtoSalvo.getNomeDoProduto());
        return produtoSalvo;
    }

    // Atualizar estoque
    @Transactional
    public Produto atualizarEstoque(Long produtoId, int quantidadeAlterada) {
        logger.info("Atualizando estoque do produto {} com quantidade {}", produtoId, quantidadeAlterada);
        
        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new ProdutoNotFoundException(produtoId));

        int novoEstoque = produto.getDisponivelEmEstoque() + quantidadeAlterada;

        if (novoEstoque < 0) {
            logger.warn("Tentativa de atualizar estoque para valor negativo: produto={}, estoqueAtual={}, alteracao={}", 
                       produto.getNomeDoProduto(), produto.getDisponivelEmEstoque(), quantidadeAlterada);
            throw new EstoqueInsuficienteException(produto.getNomeDoProduto(), 
                                                  produto.getDisponivelEmEstoque(), 
                                                  Math.abs(quantidadeAlterada));
        }

        produto.setDisponivelEmEstoque(novoEstoque);
        Produto produtoAtualizado = produtoRepository.save(produto);
        logger.info("Estoque atualizado com sucesso: produto={}, novoEstoque={}", 
                   produto.getNomeDoProduto(), novoEstoque);
        return produtoAtualizado;
    }

    // Editar dados do produto
    @Transactional
    public Produto editarProduto(Long produtoId, Produto dadosAtualizados) {
        logger.info("Editando produto com ID: {}", produtoId);
        
        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new ProdutoNotFoundException(produtoId));

        // Verificar se o novo código de barras já existe em outro produto
        if (!produto.getCodigoBarras().equals(dadosAtualizados.getCodigoBarras())) {
            if (produtoRepository.findByCodigoBarras(dadosAtualizados.getCodigoBarras()).isPresent()) {
                logger.warn("Tentativa de alterar código de barras para um que já existe: {}", dadosAtualizados.getCodigoBarras());
                throw new CodigoBarrasDuplicadoException(dadosAtualizados.getCodigoBarras());
            }
        }

        produto.setNomeDoProduto(dadosAtualizados.getNomeDoProduto());
        produto.setCodigoBarras(dadosAtualizados.getCodigoBarras());
        produto.setPrecoVenda(dadosAtualizados.getPrecoVenda());
        produto.setPrecoCusto(dadosAtualizados.getPrecoCusto());

        Produto produtoEditado = produtoRepository.save(produto);
        logger.info("Produto editado com sucesso: {}", produto.getNomeDoProduto());
        return produtoEditado;
    }

    //Deletar produto
    @Transactional
    public void deletarProduto(Long produtoId) {
        logger.info("Deletando produto com ID: {}", produtoId);
        Produto produto = produtoRepository.findById(produtoId)
            .orElseThrow(() -> new ProdutoNotFoundException(produtoId));
        try {
            // Criar produto genérico específico para este produto removido
            String codigoGenerico = "removido_" + produto.getCodigoBarras();
            String nomeGenerico = "Produto Removido (" + produto.getNomeDoProduto() + ")";
            
            Produto produtoGenerico = produtoRepository.findByCodigoBarras(codigoGenerico)
                .orElseGet(() -> {
                    Produto novo = new Produto();
                    novo.setNomeDoProduto(nomeGenerico);
                    novo.setCodigoBarras(codigoGenerico);
                    novo.setPrecoVenda(java.math.BigDecimal.ZERO);
                    novo.setPrecoCusto(java.math.BigDecimal.ZERO);
                    novo.setDisponivelEmEstoque(0);
                    return produtoRepository.save(novo);
                });
            
            // Substituir referência do produto em todas as vendas pelo produto genérico
            var itens = vendaProdutoRepository.findByProduto(produto);
            for (var item : itens) {
                item.setProduto(produtoGenerico);
                vendaProdutoRepository.save(item);
            }
            logger.info("Referências do produto {} substituídas pelo produto genérico em {} itens", produtoId, itens.size());
            
            // Por fim, remover o produto original
            produtoRepository.delete(produto);
            logger.info("Produto deletado com sucesso: ID={}", produtoId);
        } catch (DataIntegrityViolationException e) {
            logger.error("Erro de integridade ao deletar produto {}: {}", produtoId, e.getMessage());
            throw new ProdutoNaoPodeSerExcluidoException(produtoId);
        } catch (Exception e) {
            logger.error("Erro inesperado ao deletar produto {}: {}", produtoId, e.getMessage(), e);
            throw e;
        }
    }
}

