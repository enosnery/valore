package com.valore.repository;

import com.valore.domain.ItemTabelaPreco;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ItemTabelaPrecoRepository extends JpaRepository<ItemTabelaPreco, Long> {

    List<ItemTabelaPreco> findByTabelaPrecoIdOrderByProdutoNomeAsc(Long tabelaPrecoId);

    boolean existsByTabelaPrecoIdAndProdutoId(Long tabelaPrecoId, Long produtoId);

    boolean existsByProdutoId(Long produtoId);

    List<ItemTabelaPreco> findByTabelaPrecoAtivaTrueAndValidadeValorGreaterThanEqualOrderByTabelaPrecoFornecedorRazaoSocialAscProdutoNomeAsc(
            LocalDate data);
}
