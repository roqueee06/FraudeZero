package com.bradesco.auth_system.service;

import com.bradesco.auth_system.model.Compra;
import com.bradesco.auth_system.model.Suspeita;
import com.bradesco.auth_system.repository.CompraRepository;
import com.bradesco.auth_system.repository.SuspeitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CompraService {
    
    @Autowired
    private CompraRepository compraRepository;
    
    @Autowired
    private SuspeitaRepository suspeitaRepository;
    
    @Autowired
    private DetectorFraudeService detectorFraudeService;
    
    // MÉTODO CORRETO: salvarCompra
    public Compra salvarCompra(Compra compra) {
        Compra compraSalva = compraRepository.save(compra);
        
        // Detectar fraudes
        List<Suspeita> suspeitas = detectorFraudeService.detectarFraudes(compraSalva);
        
        // Salvar suspeitas detectadas
        if (!suspeitas.isEmpty()) {
            suspeitaRepository.saveAll(suspeitas);
            System.out.println(suspeitas.size() + " suspeita(s) detectada(s) para compra: " + compraSalva.getId());
        }
        
        return compraSalva;
    }
    
    // MÉTODO CORRETO: buscarComprasPorUsuario
    public List<Compra> buscarComprasPorUsuario(Long idUsuario) {
        return compraRepository.findByIdUsuario(idUsuario);
    }
    
    // Se precisar de um método para buscar todas as compras (não apenas por usuário)
    public List<Compra> buscarTodasCompras() {
        return compraRepository.findAll();
    }
}