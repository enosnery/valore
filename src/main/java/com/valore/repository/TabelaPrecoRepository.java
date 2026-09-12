package com.valore.repository;

import com.valore.domain.TabelaPreco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TabelaPrecoRepository extends JpaRepository<TabelaPreco, Long> {

    List<TabelaPreco> findByValidadeValorGreaterThanEqualOrderByFornecedorRazaoSocialAscProdutoNomeAsc(
            java.time.LocalDate data);

    boolean existsByFornecedorIdAndProdutoId(Long fornecedorId, Long produtoId);
}
