package com.bradesco.auth_system.repository;

import com.bradesco.auth_system.model.Extrato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ExtratoRepository extends JpaRepository<Extrato, Long> {
    List<Extrato> findByIdUsuarioOrderByDataTransacaoDesc(Long idUsuario);
    List<Extrato> findByIdCompra(Long idCompra);
}