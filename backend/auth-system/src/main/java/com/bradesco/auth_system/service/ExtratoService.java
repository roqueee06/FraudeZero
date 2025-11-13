package com.bradesco.auth_system.service;

import com.bradesco.auth_system.model.*;
import com.bradesco.auth_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;


@Service
public class ExtratoService {

    @Autowired
    private ExtratoRepository extratoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CompraRepository compraRepository;
    
    @Autowired
    private SuspeitaRepository suspeitaRepository;

    @Transactional
    public void registrarCompraNormal(Compra compra) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(compra.getIdUsuario());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            BigDecimal saldoAnterior = usuario.getSaldoDisponivel();
            BigDecimal saldoAtual = saldoAnterior.subtract(compra.getPrecoCompra());
            
            usuario.setSaldoDisponivel(saldoAtual);
            usuarioRepository.save(usuario);
            
            Extrato extrato = new Extrato();
            extrato.setIdUsuario(compra.getIdUsuario());
            extrato.setIdCompra(compra.getId());
            extrato.setValorCompra(compra.getPrecoCompra());
            extrato.setSaldoAnterior(saldoAnterior);
            extrato.setSaldoAtual(saldoAtual);
            extrato.setTipo("COMPRA");
            extrato.setStatus("APROVADA");
            
            extratoRepository.save(extrato);
        }
    }

    @Transactional
    public void registrarCompraSuspeita(Compra compra) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findById(compra.getIdUsuario());
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            BigDecimal saldoAnterior = usuario.getSaldoDisponivel();
            
            Extrato extrato = new Extrato();
            extrato.setIdUsuario(compra.getIdUsuario());
            extrato.setIdCompra(compra.getId());
            extrato.setValorCompra(compra.getPrecoCompra());
            extrato.setSaldoAnterior(saldoAnterior);
            extrato.setSaldoAtual(saldoAnterior);
            extrato.setTipo("FRAUDE_SUSPEITA");
            extrato.setStatus("SUSPEITA");
            
            extratoRepository.save(extrato);
        }
    }

    @Transactional
    public void aprovarCompraSuspeita(Long idCompra) {
        Optional<Extrato> extratoOpt = extratoRepository.findByIdCompra(idCompra)
                .stream().findFirst();
                
        if (extratoOpt.isPresent()) {
            Extrato extrato = extratoOpt.get();
            Optional<Usuario> usuarioOpt = usuarioRepository.findById(extrato.getIdUsuario());
            
            if (usuarioOpt.isPresent()) {
                Usuario usuario = usuarioOpt.get();
                BigDecimal saldoAnterior = usuario.getSaldoDisponivel();
                BigDecimal saldoAtual = saldoAnterior.subtract(extrato.getValorCompra());
      
                usuario.setSaldoDisponivel(saldoAtual);
                usuarioRepository.save(usuario);
                
                extrato.setSaldoAnterior(saldoAnterior);
                extrato.setSaldoAtual(saldoAtual);
                extrato.setTipo("FRAUDE_APROVADA");
                extrato.setStatus("APROVADA");
                
                extratoRepository.save(extrato);
                
                suspeitaRepository.findByIdCompra(idCompra)
                    .forEach(suspeita -> suspeitaRepository.delete(suspeita));
            }
        }
    }

    @Transactional
    public void contestarCompraSuspeita(Long idCompra) {
        Optional<Extrato> extratoOpt = extratoRepository.findByIdCompra(idCompra)
                .stream().findFirst();
                
        if (extratoOpt.isPresent()) {
            Extrato extrato = extratoOpt.get();
            
            extrato.setTipo("FRAUDE_CONTESTADA");
            extrato.setStatus("CONTESTADA");
            extratoRepository.save(extrato);
            
            suspeitaRepository.findByIdCompra(idCompra)
                .forEach(suspeita -> suspeitaRepository.delete(suspeita));
                
        }
    }

    public List<Extrato> buscarExtratoPorUsuario(Long idUsuario) {
        return extratoRepository.findByIdUsuarioOrderByDataTransacaoDesc(idUsuario);
    }
}