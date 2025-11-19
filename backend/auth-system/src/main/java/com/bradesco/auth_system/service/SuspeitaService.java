package com.bradesco.auth_system.service;

import com.bradesco.auth_system.model.Suspeita;
import com.bradesco.auth_system.repository.SuspeitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SuspeitaService {
    
    @Autowired
    private SuspeitaRepository suspeitaRepository;
    
    public List<Suspeita> buscarSuspeitasPorUsuario(Long idUsuario) {
        return suspeitaRepository.findByIdUsuario(idUsuario);
    }
    
    public void aprovarCompra(Long idCompra) {
        // Remove a suspeita da tabela (compra foi aprovada pelo usuário)
        List<Suspeita> suspeitas = suspeitaRepository.findByIdCompra(idCompra);
        if (!suspeitas.isEmpty()) {
            suspeitaRepository.deleteAll(suspeitas);
            System.out.println("Compra " + idCompra + " aprovada pelo usuário");
        }
    }
    
    public void contestarCompra(Long idCompra) {
        // Remove a suspeita e pode registrar como fraude confirmada
        List<Suspeita> suspeitas = suspeitaRepository.findByIdCompra(idCompra);
        if (!suspeitas.isEmpty()) {
            suspeitaRepository.deleteAll(suspeitas);
            System.out.println("Compra " + idCompra + " contestada pelo usuário - FRAUDE CONFIRMADA");
            // Aqui você pode adicionar lógica para bloquear cartão, notificar banco, etc.
        }
    }
}