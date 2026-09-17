package com.valore.repository;

import com.valore.domain.Tela;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelaRepository extends JpaRepository<Tela, Long> {

    Optional<Tela> findByCodigo(String codigo);
}
