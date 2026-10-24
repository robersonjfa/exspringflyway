package br.edu.unoesc.loja.service;

import br.edu.unoesc.loja.domain.Cliente;
import br.edu.unoesc.loja.domain.Pedido;
import br.edu.unoesc.loja.domain.Produto;
import br.edu.unoesc.loja.repository.ClienteRepository;
import br.edu.unoesc.loja.repository.PedidoRepository;
import br.edu.unoesc.loja.repository.ProdutoRepository;
import br.edu.unoesc.loja.web.dto.PedidoRequest;
import br.edu.unoesc.loja.web.dto.PedidoResponse;
import br.edu.unoesc.loja.web.dto.ResumoCliente;
import br.edu.unoesc.loja.web.erro.RecursoNaoEncontradoException;
import br.edu.unoesc.loja.web.erro.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository pedidos;
    private final ClienteRepository clientes;
    private final ProdutoRepository produtos;

    public PedidoService(PedidoRepository pedidos, ClienteRepository clientes, ProdutoRepository produtos) {
        this.pedidos = pedidos;
        this.clientes = clientes;
        this.produtos = produtos;
    }

    @Transactional(readOnly = true)
    public List<PedidoResponse> listar() {
        return pedidos.listarComClienteEProduto().stream().map(PedidoResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponse buscar(Long id) {
        return PedidoResponse.de(entidade(id));
    }

    @Transactional(readOnly = true)
    public List<ResumoCliente> resumo() {
        return pedidos.resumoPorCliente();
    }

    @Transactional
    public PedidoResponse criar(PedidoRequest req) {
        Cliente cliente = clienteAtivo(req.clienteId());
        Produto produto = produtos.findById(req.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto " + req.produtoId() + " não encontrado"));

        reservarEstoque(produto, req.quantidade());
        Pedido pedido = new Pedido(cliente, produto, req.quantidade(), req.observacao());
        return PedidoResponse.de(pedidos.save(pedido));
    }

    @Transactional
    public PedidoResponse atualizar(Long id, PedidoRequest req) {
        Pedido pedido = entidade(id);
        Cliente cliente = clienteAtivo(req.clienteId());
        Produto novoProduto = produtos.findById(req.produtoId())
                .orElseThrow(() -> new RecursoNaoEncontradoException("Produto " + req.produtoId() + " não encontrado"));

        // devolve o que estava reservado e reserva de novo, conforme o pedido novo
        pedido.getProduto().devolverEstoque(pedido.getQuantidade());
        reservarEstoque(novoProduto, req.quantidade());

        pedido.setCliente(cliente);
        pedido.setProduto(novoProduto);
        pedido.setQuantidade(req.quantidade());
        pedido.setObservacao(req.observacao());
        return PedidoResponse.de(pedido);
    }

    @Transactional
    public void excluir(Long id) {
        Pedido pedido = entidade(id);
        pedido.getProduto().devolverEstoque(pedido.getQuantidade());
        pedidos.delete(pedido);
    }

    private void reservarEstoque(Produto produto, int quantidade) {
        if (produto.getEstoque() < quantidade) {
            throw new RegraNegocioException("Estoque insuficiente para " + produto.getNome()
                    + ": disponível " + produto.getEstoque() + ", pedido " + quantidade + ".");
        }
        produto.baixarEstoque(quantidade);
    }

    private Cliente clienteAtivo(Long clienteId) {
        Cliente cliente = clientes.findById(clienteId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + clienteId + " não encontrado"));
        if (!cliente.isAtivo()) {
            throw new RegraNegocioException("O cliente " + cliente.getNome() + " está inativo.");
        }
        return cliente;
    }

    private Pedido entidade(Long id) {
        return pedidos.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pedido " + id + " não encontrado"));
    }
}
