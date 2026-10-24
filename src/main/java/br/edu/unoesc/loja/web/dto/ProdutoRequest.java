package br.edu.unoesc.loja.web.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProdutoRequest(
        @NotBlank(message = "nome é obrigatório")
        @Size(max = 120, message = "nome deve ter no máximo 120 caracteres")
        String nome,

        @NotNull(message = "preço é obrigatório")
        @DecimalMin(value = "0.0", message = "preço não pode ser negativo")
        @Digits(integer = 8, fraction = 2, message = "preço deve ter no máximo 2 casas decimais")
        BigDecimal preco,

        @NotNull(message = "estoque é obrigatório")
        @PositiveOrZero(message = "estoque não pode ser negativo")
        Integer estoque) {
}
