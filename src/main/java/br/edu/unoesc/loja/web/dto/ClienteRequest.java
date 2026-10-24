package br.edu.unoesc.loja.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 120, message = "nome deve ter no máximo 120 caracteres")
        String nome,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "email inválido")
        @Size(max = 160, message = "email deve ter no máximo 160 caracteres")
        String email,

        Boolean ativo) {
}
