package br.edu.unoesc.loja.service;

import br.edu.unoesc.loja.domain.Cliente;
import br.edu.unoesc.loja.repository.ClienteRepository;
import br.edu.unoesc.loja.repository.PedidoRepository;
import br.edu.unoesc.loja.web.dto.ClienteRequest;
import br.edu.unoesc.loja.web.dto.ClienteResponse;
import br.edu.unoesc.loja.web.erro.RecursoNaoEncontradoException;
import br.edu.unoesc.loja.web.erro.RegraNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clientes;
    private final PedidoRepository pedidos;

    public ClienteService(ClienteRepository clientes, PedidoRepository pedidos) {
        this.clientes = clientes;
        this.pedidos = pedidos;
    }

    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return clientes.findAllByOrderByNomeAsc().stream().map(ClienteResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public ClienteResponse buscar(Long id) {
        return ClienteResponse.de(entidade(id));
    }

    @Transactional
    public ClienteResponse criar(ClienteRequest req) {
        if (clientes.existsByEmail(req.email())) {
            throw new RegraNegocioException("Já existe um cliente com o e-mail " + req.email());
        }
        Cliente cliente = new Cliente(req.nome().trim(), req.email().trim().toLowerCase());
        if (req.ativo() != null) {
            cliente.setAtivo(req.ativo());
        }
        return ClienteResponse.de(clientes.save(cliente));
    }

    @Transactional
    public ClienteResponse atualizar(Long id, ClienteRequest req) {
        Cliente cliente = entidade(id);
        String email = req.email().trim().toLowerCase();
        clientes.findByEmail(email)
                .filter(outro -> !outro.getId().equals(id))
                .ifPresent(outro -> {
                    throw new RegraNegocioException("Já existe um cliente com o e-mail " + email);
                });
        cliente.setNome(req.nome().trim());
        cliente.setEmail(email);
        if (req.ativo() != null) {
            cliente.setAtivo(req.ativo());
        }
        return ClienteResponse.de(cliente);
    }

    @Transactional
    public void excluir(Long id) {
        Cliente cliente = entidade(id);
        if (pedidos.existsByClienteId(id)) {
            throw new RegraNegocioException("Este cliente tem pedidos e não pode ser excluído. "
                    + "Desative o cadastro (ativo = false) em vez de apagar.");
        }
        clientes.delete(cliente);
    }

    private Cliente entidade(Long id) {
        return clientes.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Cliente " + id + " não encontrado"));
    }
}
