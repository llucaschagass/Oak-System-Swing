package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * DTO para representar o relatório de Balanço Geral Financeiro.
 * Recebe dados do endpoint /api/relatorios/balanco-financeiro.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Ignora outros campos (como a lista 'itens')
public class BalancoGeralDTO {

    /** O valor monetário total de todos os produtos em estoque. */
    private BigDecimal valorTotalEstoque;

    /**
     * @return O valor total do estoque.
     */
    public BigDecimal getValorTotalEstoque() {
        return valorTotalEstoque;
    }

    /**
     * @param v O valor total do estoque.
     */
    public void setValorTotalEstoque(BigDecimal v) {
        this.valorTotalEstoque = v;
    }
}