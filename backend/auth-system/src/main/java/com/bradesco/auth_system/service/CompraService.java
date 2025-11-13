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

    @Autowired
    private ExtratoService extratoService;

    @Transactional
    public Compra salvar(Compra compra) {
        Compra compraSalva = compraRepository.save(compra);
        
        List<Suspeita> suspeitas = detectorFraudeService.detectarFraudes(compraSalva);
        
        if (!suspeitas.isEmpty()) {
            suspeitaRepository.saveAll(suspeitas);
        }

        if (suspeitas.isEmpty()) {
            extratoService.registrarCompraNormal(compraSalva);
        } else {
            extratoService.registrarCompraSuspeita(compraSalva);
        }
        
        return compraSalva;
    }

    public List<Compra> buscarComprasPorUsuario(Long idUsuario) {
        return compraRepository.findByIdUsuario(idUsuario);
    }

    public List<Compra> buscarTodasCompras() {
        return compraRepository.findAll();
    }

    public Optional<Compra> buscarPorId(Long id) {
        return compraRepository.findById(id);
    }
}