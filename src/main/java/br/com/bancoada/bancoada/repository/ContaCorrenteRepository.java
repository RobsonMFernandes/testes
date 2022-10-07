package br.com.bancoada.bancoada.repository;

import br.com.bancoada.bancoada.entity.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Integer> {
}
