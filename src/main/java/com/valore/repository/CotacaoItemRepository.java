package com.valore.repository;

import com.valore.domain.CotacaoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CotacaoItemRepository extends JpaRepository<CotacaoItem, Long> {

    boolean existsByCotacaoIdAndTabelaPrecoId(Long cotacaoId, Long tabelaPrecoId);

    void deleteByCotacaoIdAndTabelaPrecoId(Long cotacaoId, Long tabelaPrecoId);
}
