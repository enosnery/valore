package com.valore.repository;

import com.valore.domain.Cotacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CotacaoRepository extends JpaRepository<Cotacao, Long> {

    Optional<Cotacao> findByIdAndUsuarioId(Long id, Long usuarioId);

    List<Cotacao> findByUsuarioIdOrderByCriadaEmDesc(Long usuarioId);
}
