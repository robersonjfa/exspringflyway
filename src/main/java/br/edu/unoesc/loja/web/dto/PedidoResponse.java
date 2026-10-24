package br.edu.unoesc.loja.web.dto;

import br.edu.unoesc.loja.domain.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PedidoResponse(
        Long id,
        Long clienteId,
        String clienteNome,
        Long produtoId,
        String produtoNome,
        Integer quantidade,
        BigDecimal precoUnitario,
        BigDecimal total,
        String observacao,
        LocalDateTime criadoEm) {

    public static PedidoResponse de(Pedido p) {
        BigDecimal preco = p.getProduto().getPreco();
        return new PedidoResponse(
                p.getId(),
                p.getCliente().getId(),
                p.getCliente().getNome(),
                p.getProduto().getId(),
                p.getProduto().getNome(),
                p.getQuantidade(),
                preco,
                preco.multiply(BigDecimal.valueOf(p.getQuantidade())),
                p.getObservacao(),
                p.getCriadoEm());
    }
}
