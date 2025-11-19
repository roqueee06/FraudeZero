package com.bradesco.auth_system.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "suspeitas")
@Data
public class Suspeita {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "id_usuario", nullable = false)
    private Long idUsuario;
    
    @Column(name = "id_compra", nullable = false)
    private Long idCompra;
    
    @Column(name = "tipo_fraude", nullable = false)
    private String tipoFraude;
    
    @Column(name = "condicao")
    private String condicao;
    
    @Column(name = "modo_pagamento")
    private String modoPagamento;
    
    @Column(name = "data_deteccao")
    private LocalDateTime dataDeteccao;
    
    @PrePersist
    protected void onCreate() {
        dataDeteccao = LocalDateTime.now();
    }
}