package br.edu.unoesc.loja.web.dto;

import br.edu.unoesc.loja.domain.Cliente;

import java.time.LocalDateTime;

public record ClienteResponse(Long id, String nome, String email, boolean ativo, LocalDateTime criadoEm) {

    public static ClienteResponse de(Cliente c) {
        return new ClienteResponse(c.getId(), c.getNome(), c.getEmail(), c.isAtivo(), c.getCriadoEm());
    }
}
