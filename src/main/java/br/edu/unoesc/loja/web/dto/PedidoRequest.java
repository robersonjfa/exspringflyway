package br.edu.unoesc.loja.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PedidoRequest(
        @NotNull(message = "clienteId é obrigatório")
        Long clienteId,

        @NotNull(message = "produtoId é obrigatório")
        Long produtoId,

        @NotNull(message = "quantidade é obrigatória")
        @Positive(message = "quantidade deve ser maior que zero")
        Integer quantidade,

        @Size(max = 200, message = "observação deve ter no máximo 200 caracteres")
        String observacao) {
}
