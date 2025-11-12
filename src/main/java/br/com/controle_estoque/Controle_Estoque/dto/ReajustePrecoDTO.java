package br.com.controle_estoque.Controle_Estoque.dto;

import java.math.BigDecimal;

/**
 * DTO para o "payload" (carga de dados) ao reajustar preços em massa.
 * Usado como corpo da requisição POST para /api/produtos/reajustar-preco.
 */
public class ReajustePrecoDTO {

    /** O valor percentual do reajuste (ex: 10.5 para +10.5%). */
    private BigDecimal percentual;

    /**
     * Construtor padrão.
     */
    public ReajustePrecoDTO() {}

    /**
     * Constrói o DTO de reajuste de preço.
     *
     * @param percentual O valor percentual do reajuste.
     */
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