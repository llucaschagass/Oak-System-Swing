package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * DTO para representar a entidade Produto.
 * Usado para transferir dados de produtos recebidos da API (ex: GET /api/produtos).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutoDTO {

    /** O identificador único do produto. */
    private long id;

    /** O nome de exibição do produto. */
    private String nome;

    /** O preço de venda unitário. */
    private BigDecimal precoUnitario;

    /** A unidade de medida (ex: "UN", "KG", "Pacote"). */
    private String unidade;

    /** A quantidade atual em estoque. */
    private int quantidadeEmEstoque;

    /** A categoria aninhada à qual o produto pertence. */
    private CategoriaDTO categoria;

    /** O nível mínimo de estoque antes de um alerta. */
    private int quantidadeMinima;

    /** O nível máximo de estoque recomendado. */
    private int quantidadeMaxima;

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public int getQuantidadeEmEstoque() { return quantidadeEmEstoque; }
    public void setQuantidadeEmEstoque(int quantidadeEmEstoque) { this.quantidadeEmEstoque = quantidadeEmEstoque; }

    public CategoriaDTO getCategoria() { return categoria; }
    public void setCategoria(CategoriaDTO categoria) { this.categoria = categoria; }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }
    public void setQuantidadeMinima(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public int getQuantidadeMaxima() {
        return quantidadeMaxima;
    }
    public void setQuantidadeMaxima(int quantidadeMaxima) {
        this.quantidadeMaxima = quantidadeMaxima;
    }

    /**
     * Retorna o nome do produto.
     * Usado pelo JComboBox para exibir o texto correto no dropdown.
     * @return O nome do produto.
     */
    @Override
    public String toString() {
        return this.nome;
    }
}