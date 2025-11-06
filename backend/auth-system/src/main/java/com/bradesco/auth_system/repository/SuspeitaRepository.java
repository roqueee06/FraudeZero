package com.bradesco.auth_system.repository;

import com.bradesco.auth_system.model.Suspeita;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SuspeitaRepository extends JpaRepository<Suspeita, Long> {
    List<Suspeita> findByIdUsuario(Long idUsuario);
    List<Suspeita> findByIdCompra(Long idCompra);
}