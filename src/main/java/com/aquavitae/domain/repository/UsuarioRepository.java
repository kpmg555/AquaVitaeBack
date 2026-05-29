package com.aquavitae.domain.repository;

import com.aquavitae.domain.models.Usuario;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UsuarioRepository {
    List<Usuario> findActivosConDetalle(Integer idEmpresa, int page, int size);

    long countActivos(Integer idEmpresa);

    long countTotal(Integer idEmpresa);

    Optional<Usuario> findById(Integer id);

    Optional<Usuario> findByUuid(String uuid);

    boolean existsByCorreo(String correo);

    Usuario save(Usuario usuario);

    void desactivar(Integer id);

    Map<Integer, List<String>> findModulosPersonalizadosByIds(List<Integer> ids);
}