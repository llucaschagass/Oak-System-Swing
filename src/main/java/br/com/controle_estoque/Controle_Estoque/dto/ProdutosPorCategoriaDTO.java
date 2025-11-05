package br.com.controle_estoque.Controle_Estoque.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutosPorCategoriaDTO {
    private long quantidadeProdutos;
    // Getters e Setters
    public long getQuantidadeProdutos() { return quantidadeProdutos; }
    public void setQuantidadeProdutos(long q) { this.quantidadeProdutos = q; }
}