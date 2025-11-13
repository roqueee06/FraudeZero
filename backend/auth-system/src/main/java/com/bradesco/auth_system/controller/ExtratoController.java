package com.bradesco.auth_system.controller;

import com.bradesco.auth_system.model.Extrato;
import com.bradesco.auth_system.service.ExtratoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extrato")
@CrossOrigin("*")
public class ExtratoController {

    @Autowired
    private ExtratoService extratoService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Extrato>> getExtratoUsuario(@PathVariable Long idUsuario) {
        List<Extrato> extrato = extratoService.buscarExtratoPorUsuario(idUsuario);
        return ResponseEntity.ok(extrato);
    }

    @PostMapping("/aprovar/{idCompra}")
    public ResponseEntity<String> aprovarCompra(@PathVariable Long idCompra) {
        try {
            extratoService.aprovarCompraSuspeita(idCompra);
            return ResponseEntity.ok("Compra aprovada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    @PostMapping("/contestar/{idCompra}")
    public ResponseEntity<String> contestarCompra(@PathVariable Long idCompra) {
        try {
            extratoService.contestarCompraSuspeita(idCompra);
            return ResponseEntity.ok("Compra contestada com sucesso!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }
}