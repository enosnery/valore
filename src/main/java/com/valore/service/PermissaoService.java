package com.valore.service;

import com.valore.domain.PermissaoUsuario;
import com.valore.domain.Tela;
import com.valore.domain.Usuario;
import com.valore.repository.PermissaoUsuarioRepository;
import com.valore.repository.TelaRepository;
import com.valore.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PermissaoService {

    private final PermissaoUsuarioRepository permissaoRepository;
    private final TelaRepository telaRepository;
    private final UsuarioRepository usuarioRepository;

    public PermissaoService(PermissaoUsuarioRepository permissaoRepository, TelaRepository telaRepository,
                            UsuarioRepository usuarioRepository) {
        this.permissaoRepository = permissaoRepository;
        this.telaRepository = telaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Tela> telasDoUsuario(Long usuarioId) {
        return permissaoRepository.findByUsuarioId(usuarioId).stream()
                .map(PermissaoUsuario::getTela)
                .toList();
    }

    @Transactional(readOnly = true)
    public boolean possuiAcesso(Long usuarioId, String codigoTela) {
        if (usuarioId == null) {
            return false;
        }
        return permissaoRepository.existsByUsuarioIdAndTelaCodigo(usuarioId, codigoTela);
    }

    @Transactional
    public void substituirPermissoes(Long usuarioId, List<Long> telaIds) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));
        permissaoRepository.deleteByUsuarioId(usuarioId);
        for (Long telaId : telaIds) {
            Tela tela = telaRepository.findById(telaId)
                    .orElseThrow(() -> new IllegalArgumentException("Tela não encontrada."));
            permissaoRepository.save(new PermissaoUsuario(usuario, tela));
        }
    }
}
