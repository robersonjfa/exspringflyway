package br.edu.unoesc.loja.repository;

import br.edu.unoesc.loja.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    List<Cliente> findAllByOrderByNomeAsc();

    Optional<Cliente> findByEmail(String email);

    boolean existsByEmail(String email);
}
