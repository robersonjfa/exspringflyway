package br.edu.unoesc.loja.service;

import br.edu.unoesc.loja.domain.Produto;
import br.edu.unoesc.loja.repository.PedidoRepository;
import br.edu.unoesc.loja.repository.ProdutoRepository;
import br.edu.unoesc.loja.web.dto.ProdutoRequest;
import br.edu.unoesc.loja.web.dto.ProdutoResponse;
import br.edu.unoesc.loja.web.erro.RecursoNaoEncontradoException;
import br.edu.unoesc.loja.web.erro.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProdutoService {

    private final ProdutoRepository produtos;
    private final PedidoRepository pedidos;

    public ProdutoService(ProdutoRepository produtos, PedidoRepository pedidos) {
        this.produtos = produtos;
        this.pedidos = pedidos;
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listar() {
        return produtos.findAllByOrderByNomeAsc().stream().map(ProdutoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public List<ProdutoResponse> listarSemEstoque() {
        return produtos.listarSemEstoque().stream().map(ProdutoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ProdutoResponse buscar(Long id) {
        return ProdutoResponse.de(entidade(id));
    }

    @Transactional
    public ProdutoResponse criar(ProdutoRequest req) {
        Produto produto = new Produto(req.nome().trim(), req.preco(), req.estoque());
        return ProdutoResponse.de(produtos.save(produto));
    }

    @Transactional
    public ProdutoResponse atualizar(Long id, ProdutoRequest req) {
        Produto produto = entidade(id);
        produto.setNome(req.nome().trim());
        produto.setPreco(req.preco());
        produto.setEstoque(req.estoque());
        return ProdutoResponse.de(produto);
    }

    @Transactional
    public void excluir(Long id) {
        Produto produto = entidade(id);
        if (pedidos.existsByProdutoId(id)) {
            throw new RegraNegocioException("Este produto já foi usado em pedidos e não pode ser excluído.");
        }
        produtos.delete(produto);
    }

    Produto entidade(Long id) {
        return produtos.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto " + id + " não encontrado"));
    }
}
