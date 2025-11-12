package br.com.controle_estoque.Controle_Estoque.dto;

import java.math.BigDecimal;

public class ReajustePrecoDTO {
    private BigDecimal percentual;

    public ReajustePrecoDTO() {}

    public ReajustePrecoDTO(BigDecimal percentual) {
        this.percentual = percentual;
    }

    // Getters e Setters
    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }
}