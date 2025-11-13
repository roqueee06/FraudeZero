package com.bradesco.auth_system.controller;

import com.bradesco.auth_system.model.*;
import com.bradesco.auth_system.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class AdminController {

    @Autowired
    private CompraRepository compraRepository;
    
    @Autowired
    private SuspeitaRepository suspeitaRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private ExtratoRepository extratoRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        long totalUsuarios = usuarioRepository.count();
        long totalCompras = compraRepository.count();
        long totalSuspeitas = suspeitaRepository.count();
        
        long fraudesConfirmadas = extratoRepository.findAll().stream()
                .filter(extrato -> "CONTESTADA".equals(extrato.getStatus()) || "FRAUDE_CONTESTADA".equals(extrato.getTipo()))
                .count();
        
        List<Suspeita> todasSuspeitas = suspeitaRepository.findAll();
        Map<String, Long> fraudesPorTipo = new HashMap<>();
        for (Suspeita suspeita : todasSuspeitas) {
            String tipo = suspeita.getTipoFraude();
            fraudesPorTipo.put(tipo, fraudesPorTipo.getOrDefault(tipo, 0L) + 1);
        }
        
        BigDecimal valorTotalSuspeitas = todasSuspeitas.stream()
            .map(suspeita -> {
                return compraRepository.findById(suspeita.getIdCompra())
                    .map(Compra::getPrecoCompra)
                    .orElse(BigDecimal.ZERO);
            })
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        double taxaFraude = totalCompras > 0 ? (double) fraudesConfirmadas / totalCompras * 100 : 0;
        
        dashboard.put("totalUsuarios", totalUsuarios);
        dashboard.put("totalCompras", totalCompras);
        dashboard.put("totalSuspeitas", totalSuspeitas);
        dashboard.put("fraudesConfirmadas", fraudesConfirmadas);
        dashboard.put("fraudesPorTipo", fraudesPorTipo);
        dashboard.put("valorTotalSuspeitas", valorTotalSuspeitas);
        dashboard.put("taxaFraude", taxaFraude);
        
        return ResponseEntity.ok(dashboard);
    }

    @GetMapping("/relatorio-fraudes")
    public ResponseEntity<List<Map<String, Object>>> getRelatorioFraudes() {
        List<Suspeita> suspeitas = suspeitaRepository.findAll();
        
        List<Map<String, Object>> relatorio = suspeitas.stream().map(suspeita -> {
            Map<String, Object> item = new HashMap<>();
            Compra compra = compraRepository.findById(suspeita.getIdCompra()).orElse(null);
            Usuario usuario = usuarioRepository.findById(suspeita.getIdUsuario()).orElse(null);
            
            item.put("idSuspeita", suspeita.getId());
            item.put("idCompra", suspeita.getIdCompra());
            item.put("usuario", usuario != null ? usuario.getNome() : "N/A");
            item.put("cpfUsuario", usuario != null ? usuario.getCpf() : "N/A");
            item.put("tipoFraude", suspeita.getTipoFraude());
            item.put("condicao", suspeita.getCondicao());
            item.put("modoPagamento", suspeita.getModoPagamento());
            item.put("valor", compra != null ? compra.getPrecoCompra() : BigDecimal.ZERO);
            item.put("dataDeteccao", suspeita.getDataDeteccao());
            
            boolean isFraudeConfirmada = extratoRepository.findAll().stream()
                    .anyMatch(extrato -> extrato.getIdCompra().equals(suspeita.getIdCompra()) && 
                            ("CONTESTADA".equals(extrato.getStatus()) || "FRAUDE_CONTESTADA".equals(extrato.getTipo())));
            
            item.put("status", isFraudeConfirmada ? "CONTESTADA" : "Pendente");
            
            return item;
        }).toList();
        
        return ResponseEntity.ok(relatorio);
    }

    @GetMapping("/grafico-fraudes-mensal")
    public ResponseEntity<Map<String, Long>> getFraudesMensal() {
        List<Suspeita> suspeitas = suspeitaRepository.findAll();
        
        Map<String, Long> fraudesPorMes = new HashMap<>();
        
        for (Suspeita suspeita : suspeitas) {
            String mesAno = suspeita.getDataDeteccao().getMonth().toString() + "/" + suspeita.getDataDeteccao().getYear();
            fraudesPorMes.put(mesAno, fraudesPorMes.getOrDefault(mesAno, 0L) + 1);
        }
        
        return ResponseEntity.ok(fraudesPorMes);
    }

    @GetMapping("/usuarios")
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/compras")
    public ResponseEntity<List<Compra>> getAllCompras() {
        List<Compra> compras = compraRepository.findAll();
        return ResponseEntity.ok(compras);
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<Map<String, Object>> getUsuarioDetalhes(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id).orElse(null);
        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }
        
        List<Compra> comprasUsuario = compraRepository.findByIdUsuario(id);
        List<Suspeita> suspeitasUsuario = suspeitaRepository.findByIdUsuario(id);
        
        long fraudesConfirmadasUsuario = extratoRepository.findAll().stream()
                .filter(extrato -> extrato.getIdUsuario().equals(id) && 
                        ("CONTESTADA".equals(extrato.getStatus()) || "FRAUDE_CONTESTADA".equals(extrato.getTipo())))
                .count();
        
        Map<String, Object> detalhes = new HashMap<>();
        detalhes.put("usuario", usuario);
        detalhes.put("totalCompras", comprasUsuario.size());
        detalhes.put("totalSuspeitas", suspeitasUsuario.size());
        detalhes.put("fraudesConfirmadas", fraudesConfirmadasUsuario);
        detalhes.put("compras", comprasUsuario);
        detalhes.put("suspeitas", suspeitasUsuario);
        
        return ResponseEntity.ok(detalhes);
    }
}