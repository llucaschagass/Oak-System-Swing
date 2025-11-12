package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO auxiliar para o Relatório de Maiores Movimentações.
 * Representa um produto e o total movimentado (seja entrada ou saída).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoMovimentacaoDTO {

    /** O nome do produto. */
    private String nomeProduto;

    /** O somatório total da quantidade movimentada. */
    private long totalMovimentado;

    // Getters e Setters
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public long getTotalMovimentado() { return totalMovimentado; }
    public void setTotalMovimentado(long totalMovimentado) { this.totalMovimentado = totalMovimentado; }
}