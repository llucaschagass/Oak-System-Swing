package br.com.controle_estoque.Controle_Estoque.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BalancoGeralDTO {
    private BigDecimal valorTotalEstoque;
    // Getters e Setters
    public BigDecimal getValorTotalEstoque() { return valorTotalEstoque; }
    public void setValorTotalEstoque(BigDecimal v) { this.valorTotalEstoque = v; }
}