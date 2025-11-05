package br.com.controle_estoque.Controle_Estoque.dto;

import java.math.BigDecimal;

public class ProdutoPayloadDTO {
    private String nome;
    private BigDecimal precoUnitario;
    private String unidade;
    private int quantidadeEmEstoque;
    private int quantidadeMinima;
    private int quantidadeMaxima;
    private CategoriaIdDTO categoria;

    public ProdutoPayloadDTO(String nome, BigDecimal precoUnitario, String unidade,
                             int quantidadeEmEstoque, int quantidadeMinima,
                             int quantidadeMaxima, long categoriaId) {
        this.nome = nome;
        this.precoUnitario = precoUnitario;
        this.unidade = unidade;
        this.quantidadeEmEstoque = quantidadeEmEstoque;
        this.quantidadeMinima = quantidadeMinima;
        this.quantidadeMaxima = quantidadeMaxima;
        this.categoria = new CategoriaIdDTO(categoriaId);
    }

    public String getNome() { return nome; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public String getUnidade() { return unidade; }
    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
    public int getQuantidadeMinima() { return quantidadeMinima; }
    public int getQuantidadeMaxima() { return quantidadeMaxima; }
    public CategoriaIdDTO getCategoria() { return categoria; }
}