package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoMovimentacaoDTO {
    private String nomeProduto;
    private long totalMovimentado;

    // Getters e Setters
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }
    public long getTotalMovimentado() { return totalMovimentado; }
    public void setTotalMovimentado(long totalMovimentado) { this.totalMovimentado = totalMovimentado; }
}