package com.bradesco.auth_system.service;

import com.bradesco.auth_system.model.Compra;
import com.bradesco.auth_system.model.Suspeita;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class DetectorFraudeService {

    public List<Suspeita> detectarFraudes(Compra compra) {
        List<Suspeita> suspeitas = new ArrayList<>();

        BigDecimal limiteRazaoPreco = new BigDecimal("4.0");
        BigDecimal limiteDistanciaCasa = new BigDecimal("100.0");
        BigDecimal limiteUltimaTrans = new BigDecimal("30.0");
        BigDecimal limitePrecoExtremo = new BigDecimal("20.0");
        BigDecimal limiteDistanciaExtrema = new BigDecimal("1000.0");
        BigDecimal limiteUltimaTransExtrema = new BigDecimal("500.0");

        boolean eFraude = false;
        String tipoFraude = "";
        String condicao = "";
        String modoPagamento = compra.getPedidoOnline() ? "Compra Online" : "Compra Presencial";

        // REGRA 1: Compra Online sem PIN + múltiplas condições
        if (!compra.getUsouSenha() && compra.getPedidoOnline()) {
            int condicoesSuspeitas = 0;
            
            // Verificar razão de preço (precisamos do histórico do usuário para isso)
            // Por enquanto, vamos usar apenas o preço absoluto
            if (compra.getPrecoCompra().compareTo(new BigDecimal("1000")) > 0) {
                condicoesSuspeitas++;
                condicao = "Preço Alto";
            }
            if (compra.getDistanciaDaCasa() != null && 
                compra.getDistanciaDaCasa().compareTo(limiteDistanciaCasa) > 0) {
                condicoesSuspeitas++;
                condicao = "Distância da Casa";
            }
            if (compra.getDistanciaDaUltimaTransacao() != null && 
                compra.getDistanciaDaUltimaTransacao().compareTo(limiteUltimaTrans) > 0) {
                condicoesSuspeitas++;
                condicao = "Distância da Última Compra";
            }
            if (!compra.getUsouChip()) {
                condicoesSuspeitas++;
            }
            
            if (condicoesSuspeitas >= 2) {
                eFraude = true;
                tipoFraude = "Compra Online Suspeita";
            }
        } 
        // REGRA 2: Preço extremamente alto
        else if (compra.getPrecoCompra().compareTo(new BigDecimal("5000")) > 0) {
            eFraude = true;
            tipoFraude = "Preço Extremamente Alto";
            condicao = "Valor acima de R$ 5000";
        } 
        // REGRA 3: Distância extrema da casa
        else if (compra.getDistanciaDaCasa() != null && 
                 compra.getDistanciaDaCasa().compareTo(limiteDistanciaExtrema) > 0) {
            eFraude = true;
            tipoFraude = "Distância Extrema da Casa";
            condicao = "Mais de 1000km da residência";
        } 
        // REGRA 4: Distância extrema da última transação
        else if (compra.getDistanciaDaUltimaTransacao() != null && 
                 compra.getDistanciaDaUltimaTransacao().compareTo(limiteUltimaTransExtrema) > 0) {
            eFraude = true;
            tipoFraude = "Distância Extrema da Última Compra";
            condicao = "Mais de 500km da última transação";
        }

        if (eFraude) {
            Suspeita suspeita = new Suspeita();
            suspeita.setIdUsuario(compra.getIdUsuario());
            suspeita.setIdCompra(compra.getId());
            suspeita.setTipoFraude(tipoFraude);
            suspeita.setCondicao(condicao);
            suspeita.setModoPagamento(modoPagamento);
            
            suspeitas.add(suspeita);
        }

        return suspeitas;
    }
}