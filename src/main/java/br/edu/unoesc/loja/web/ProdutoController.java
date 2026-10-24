package br.edu.unoesc.loja.web;

import br.edu.unoesc.loja.service.ProdutoService;
import br.edu.unoesc.loja.web.dto.ProdutoRequest;
import br.edu.unoesc.loja.web.dto.ProdutoResponse;
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
@RequestMapping("/api/produtos")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProdutoResponse> listar() {
        return service.listar();
    }

    @GetMapping("/sem-estoque")
    public List<ProdutoResponse> semEstoque() {
        return service.listarSemEstoque();
    }

    @GetMapping("/{id}")
    public ProdutoResponse buscar(@PathVariable Long id) {
        return service.buscar(id);
    }

    @PostMapping
    public ResponseEntity<ProdutoResponse> criar(@Valid @RequestBody ProdutoRequest req) {
        ProdutoResponse criado = service.criar(req);
        URI local = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}").buildAndExpand(criado.id()).toUri();
        return ResponseEntity.created(local).body(criado);
    }

    @PutMapping("/{id}")
    public ProdutoResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProdutoRequest req) {
        return service.atualizar(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
