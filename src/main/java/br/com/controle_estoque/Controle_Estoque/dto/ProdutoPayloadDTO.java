package br.com.controle_estoque.Controle_Estoque.dto;

import java.math.BigDecimal;

/**
 * DTO para o payload ao criar ou atualizar um Produto.
 * Usado como corpo da requisição POST ou PUT para /api/produtos.
 */
public class ProdutoPayloadDTO {

    /** O nome de exibição do produto. */
    private String nome;

    /** O preço de venda unitário. */
    private BigDecimal precoUnitario;

    /** A unidade de medida (ex: "UN", "KG", "Pacote"). */
    private String unidade;

    /** A quantidade atual em estoque. */
    private int quantidadeEmEstoque;

    /** O nível mínimo de estoque antes de um alerta. */
    private int quantidadeMinima;

    /** O nível máximo de estoque recomendado. */
    private int quantidadeMaxima;

    /** Objeto aninhado contendo o ID da categoria: {"id": 1} */
    private CategoriaIdDTO categoria;

    /**
     * Constrói o DTO de payload do produto.
     *
     * @param nome O nome do produto.
     * @param precoUnitario O preço unitário.
     * @param unidade A unidade de medida.
     * @param quantidadeEmEstoque A quantidade em estoque.
     * @param quantidadeMinima O estoque mínimo.
     * @param quantidadeMaxima O estoque máximo.
     * @param categoriaId O ID da categoria associada.
     */
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

    // Getters
    public String getNome() { return nome; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public String getUnidade() { return unidade; }
    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
    public int getQuantidadeMinima() { return quantidadeMinima; }
    public int getQuantidadeMaxima() { return quantidadeMaxima; }
    public CategoriaIdDTO getCategoria() { return categoria; }
}