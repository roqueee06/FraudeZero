package com.bradesco.auth_system.controller;

import com.bradesco.auth_system.model.Compra;
import com.bradesco.auth_system.service.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/compras")
@CrossOrigin("*")
public class CompraController {

    @Autowired
    private CompraService compraService;

    // MÉTODO CORRIGIDO - usa "salvar" em vez de "salvarCompra"
    @PostMapping
    public ResponseEntity<?> criarCompra(@RequestBody Compra compra) {
        try {
            System.out.println("Compra recebida: " + compra);
            Compra novaCompra = compraService.salvar(compra);
            return ResponseEntity.ok(novaCompra);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Erro: " + e.getMessage());
        }
    }

    // MÉTODO CORRIGIDO - busca compras por usuário
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Compra>> getComprasPorUsuario(@PathVariable Long idUsuario) {
        List<Compra> compras = compraService.buscarComprasPorUsuario(idUsuario);
        return ResponseEntity.ok(compras);
    }

    // Método para todas as compras
    @GetMapping
    public ResponseEntity<List<Compra>> getAllCompras() {
        List<Compra> compras = compraService.buscarTodasCompras();
        return ResponseEntity.ok(compras);
    }
}