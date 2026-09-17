package com.valore.service;

import com.valore.domain.Cotacao;
import com.valore.domain.CotacaoItem;
import com.valore.domain.ItemTabelaPreco;
import com.valore.domain.TipoUsuario;
import com.valore.domain.Usuario;
import com.valore.repository.CotacaoItemRepository;
import com.valore.repository.CotacaoRepository;
import com.valore.repository.ItemTabelaPrecoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CotacaoService {

    private final CotacaoRepository cotacaoRepository;
    private final CotacaoItemRepository itemRepository;
    private final ItemTabelaPrecoRepository itemTabelaPrecoRepository;

    public CotacaoService(CotacaoRepository cotacaoRepository,
                          CotacaoItemRepository itemRepository,
                          ItemTabelaPrecoRepository itemTabelaPrecoRepository) {
        this.cotacaoRepository = cotacaoRepository;
        this.itemRepository = itemRepository;
        this.itemTabelaPrecoRepository = itemTabelaPrecoRepository;
    }

    @Transactional
    public Cotacao iniciar(Usuario usuario) {
        if (usuario.getTipo() != TipoUsuario.COMPRADOR) {
            throw new IllegalArgumentException("Apenas usuários compradores podem criar cotações.");
        }
        Cotacao cotacao = new Cotacao();
        cotacao.setUsuario(usuario);
        return cotacaoRepository.save(cotacao);
    }

    @Transactional(readOnly = true)
    public Cotacao buscarDoUsuario(Long id, Long usuarioId) {
        return cotacaoRepository.findByIdAndUsuarioId(id, usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Cotação não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Cotacao> listarDoUsuario(Long usuarioId) {
        return cotacaoRepository.findByUsuarioIdOrderByCriadaEmDesc(usuarioId);
    }

    @Transactional
    public void salvar(Long cotacaoId, Long usuarioId) {
        Cotacao cotacao = buscarDoUsuario(cotacaoId, usuarioId);
        if (cotacao.getItens().isEmpty()) {
            throw new IllegalArgumentException("Adicione pelo menos um item antes de salvar a cotação.");
        }
        cotacaoRepository.save(cotacao);
    }

    @Transactional
    public void excluir(Long cotacaoId, Long usuarioId) {
        Cotacao cotacao = buscarDoUsuario(cotacaoId, usuarioId);
        cotacaoRepository.delete(cotacao);
    }

    @Transactional
    public void adicionarItem(Long cotacaoId, Long usuarioId, Long itemTabelaPrecoId) {
        Cotacao cotacao = buscarDoUsuario(cotacaoId, usuarioId);
        ItemTabelaPreco itemTabelaPreco = itemTabelaPrecoRepository.findById(itemTabelaPrecoId)
                .orElseThrow(() -> new IllegalArgumentException("Item de tabela de preço não encontrado."));
        if (!itemTabelaPreco.getTabelaPreco().isAtiva()) {
            throw new IllegalArgumentException("Esta tabela de preço não está ativa.");
        }
        if (itemTabelaPreco.getValidadeValor().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("O valor selecionado está vencido.");
        }
        if (!itemRepository.existsByCotacaoIdAndItemTabelaPrecoId(cotacaoId, itemTabelaPrecoId)) {
            CotacaoItem item = new CotacaoItem();
            item.setCotacao(cotacao);
            item.setItemTabelaPreco(itemTabelaPreco);
            itemRepository.save(item);
        }
    }

    @Transactional
    public void removerItem(Long cotacaoId, Long usuarioId, Long itemTabelaPrecoId) {
        buscarDoUsuario(cotacaoId, usuarioId);
        itemRepository.deleteByCotacaoIdAndItemTabelaPrecoId(cotacaoId, itemTabelaPrecoId);
    }

    @Transactional(readOnly = true)
    public List<ItemTabelaPreco> listarItensVigentes() {
        return itemTabelaPrecoRepository
                .findByTabelaPrecoAtivaTrueAndValidadeValorGreaterThanEqualOrderByTabelaPrecoFornecedorRazaoSocialAscProdutoNomeAsc(
                        LocalDate.now());
    }
}
