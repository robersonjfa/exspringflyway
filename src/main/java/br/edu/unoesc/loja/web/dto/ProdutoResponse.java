package br.edu.unoesc.loja.web.dto;

import br.edu.unoesc.loja.domain.Produto;

import java.math.BigDecimal;

public record ProdutoResponse(Long id, String nome, BigDecimal preco, Integer estoque) {

    public static ProdutoResponse de(Produto p) {
        return new ProdutoResponse(p.getId(), p.getNome(), p.getPreco(), p.getEstoque());
    }
}
