package br.edu.unoesc.loja.web;

import br.edu.unoesc.loja.service.PedidoService;
import br.edu.unoesc.loja.web.dto.PedidoRequest;
import br.edu.unoesc.loja.web.dto.PedidoResponse;
import br.edu.unoesc.loja.web.dto.ResumoCliente;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PedidoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/resumo")
    public List<ResumoCliente> resumo() {
        return service.resumo();
    }

    @GetMapping("/{id}")
    public PedidoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> criar(@Valid @RequestBody PedidoRequest req) {
        PedidoResponse criado = service.criar(req);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public PedidoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PedidoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
