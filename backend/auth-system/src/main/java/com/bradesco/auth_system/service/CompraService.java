package com.bradesco.auth_system.service;

import com.bradesco.auth_system.model.Compra;
import com.bradesco.auth_system.model.Suspeita;
import com.bradesco.auth_system.repository.CompraRepository;
import com.bradesco.auth_system.repository.SuspeitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CompraService {

    @Autowired
    private CompraRepository compraRepository;
    
    @Autowired
    private SuspeitaRepository suspeitaRepository;
    
    @Autowired
    private DetectorFraudeService detectorFraudeService;

    // MÉTODO CORRIGIDO - deve se chamar "salvar"
    @Transactional
    public Compra salvar(Compra compra) {
        // Salva a compra primeiro para gerar o ID
        Compra compraSalva = compraRepository.save(compra);
        
        // Detecta fraudes
        List<Suspeita> suspeitas = detectorFraudeService.detectarFraudes(compraSalva);
        
        // Salva as suspeitas se houver
        if (!suspeitas.isEmpty()) {
            suspeitaRepository.saveAll(suspeitas);
        }
        
        return compraSalva;
    }

    // MÉTODO PARA BUSCAR COMPRAS POR USUÁRIO
    public List<Compra> buscarComprasPorUsuario(Long idUsuario) {
        return compraRepository.findByIdUsuario(idUsuario);
    }

    // MÉTODO PARA BUSCAR TODAS AS COMPRAS
    public List<Compra> buscarTodasCompras() {
        return compraRepository.findAll();
    }

    // MÉTODO PARA BUSCAR COMPRA POR ID
    public Optional<Compra> buscarPorId(Long id) {
        return compraRepository.findById(id);
    }
}