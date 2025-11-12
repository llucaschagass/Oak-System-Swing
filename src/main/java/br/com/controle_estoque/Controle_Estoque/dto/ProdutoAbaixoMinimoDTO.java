package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para representar os dados do Relatório de Produtos Abaixo do Mínimo.
 * Recebe dados do endpoint /api/relatorios/produtos-abaixo-minimo.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoAbaixoMinimoDTO {

    /** O nome de exibição do produto. */
    private String nomeProduto;

    /** O limite mínimo de estoque definido para este produto. */
    private int quantidadeMinima;

    /** A quantidade real de estoque atual. */
    private int quantidadeEmEstoque;

    // Getters e Setters
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public int getQuantidadeMinima() { return quantidadeMinima; }
    public void setQuantidadeMinima(int quantidadeMinima) { this.quantidadeMinima = quantidadeMinima; }

    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
    public void setQuantidadeEmEstoque(int quantidadeEmEstoque) { this.quantidadeEmEstoque = quantidadeEmEstoque; }
}