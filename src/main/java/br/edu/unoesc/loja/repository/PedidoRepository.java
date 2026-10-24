package br.edu.unoesc.loja.repository;

import br.edu.unoesc.loja.domain.Pedido;
import br.edu.unoesc.loja.web.dto.ResumoCliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /** join fetch: traz cliente e produto na mesma consulta (evita N+1). */
    @Query("""
            select p from Pedido p
              join fetch p.cliente
              join fetch p.produto
             order by p.id
            """)
    List<Pedido> listarComClienteEProduto();

    boolean existsByClienteId(Long clienteId);

    boolean existsByProdutoId(Long produtoId);

    @Query("""
            select new br.edu.unoesc.loja.web.dto.ResumoCliente(
                     c.nome,
                     count(p),
                     sum(p.quantidade),
                     sum(p.quantidade * p.produto.preco))
              from Pedido p
              join p.cliente c
             group by c.nome
             order by c.nome
            """)
    List<ResumoCliente> resumoPorCliente();
}
