package com.bradesco.auth_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "extrato")
@Data
public class Extrato {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;
    
    @Column(name = "id_compra", nullable = false)
    private Long idCompra;
    
    @Column(name = "valor_compra", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorCompra;
    
    @Column(name = "saldo_anterior", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoAnterior;
    
    @Column(name = "saldo_atual", nullable = false, precision = 10, scale = 2)
    private BigDecimal saldoAtual;
    
    @Column(name = "data_transacao")
    private LocalDateTime dataTransacao;
    
    @Column(name = "tipo", nullable = false)
    private String tipo;
    
    @Column(name = "status", nullable = false)
    private String status;
    
    @PrePersist
    protected void onCreate() {
        if (dataTransacao == null) {
            dataTransacao = LocalDateTime.now();
        }
    }
}