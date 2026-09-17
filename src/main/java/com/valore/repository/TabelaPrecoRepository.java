package com.valore.repository;

import com.valore.domain.TabelaPreco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TabelaPrecoRepository extends JpaRepository<TabelaPreco, Long> {

    List<TabelaPreco> findByFornecedorIdOrderByNomeAsc(Long fornecedorId);

    Optional<TabelaPreco> findByIdAndFornecedorId(Long id, Long fornecedorId);

    List<TabelaPreco> findByFornecedorIdAndAtivaTrueAndIdNot(Long fornecedorId, Long id);

    boolean existsByFornecedorId(Long fornecedorId);
}
