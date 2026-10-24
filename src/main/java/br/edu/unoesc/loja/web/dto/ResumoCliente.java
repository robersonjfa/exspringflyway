package br.edu.unoesc.loja.web.dto;

import java.math.BigDecimal;

public record ResumoCliente(String cliente, Long pedidos, Long itens, BigDecimal total) {
}
