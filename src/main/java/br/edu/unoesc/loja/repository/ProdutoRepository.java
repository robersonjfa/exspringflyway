package br.edu.unoesc.loja.repository;

import br.edu.unoesc.loja.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByOrderByNomeAsc();

    /** Consulta JPQL escrita à mão: produto sem estoque. */
    @Query("select p from Produto p where p.estoque = 0 order by p.nome")
    List<Produto> listarSemEstoque();
}
