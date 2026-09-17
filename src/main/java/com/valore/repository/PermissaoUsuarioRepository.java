package com.valore.repository;

import com.valore.domain.PermissaoUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PermissaoUsuarioRepository extends JpaRepository<PermissaoUsuario, Long> {

    List<PermissaoUsuario> findByUsuarioId(Long usuarioId);

    boolean existsByUsuarioIdAndTelaCodigo(Long usuarioId, String codigoTela);

    void deleteByUsuarioId(Long usuarioId);
}
