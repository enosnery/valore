package com.valore.repository;

import com.valore.domain.CotacaoItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CotacaoItemRepository extends JpaRepository<CotacaoItem, Long> {

    boolean existsByCotacaoIdAndItemTabelaPrecoId(Long cotacaoId, Long itemTabelaPrecoId);

    void deleteByCotacaoIdAndItemTabelaPrecoId(Long cotacaoId, Long itemTabelaPrecoId);

    boolean existsByItemTabelaPrecoTabelaPrecoId(Long tabelaPrecoId);

    boolean existsByItemTabelaPrecoId(Long itemTabelaPrecoId);
}
