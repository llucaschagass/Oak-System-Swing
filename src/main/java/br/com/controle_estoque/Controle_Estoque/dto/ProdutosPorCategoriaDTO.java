package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para representar os dados do Relatório de Contagem de Produtos por Categoria.
 * Recebe dados do endpoint /api/relatorios/produtos-por-categoria.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutosPorCategoriaDTO {

    /** O nome da categoria. */
    private String nomeCategoria;

    /** O número total de produtos associados a esta categoria. */
    private long quantidadeProdutos;

    // Getters e Setters
    public String getNomeCategoria() {
        return nomeCategoria;
    }
    public void setNomeCategoria(String nomeCategoria) {
        this.nomeCategoria = nomeCategoria;
    }

    public long getQuantidadeProdutos() {
        return quantidadeProdutos;
    }
    public void setQuantidadeProdutos(long q) {
        this.quantidadeProdutos = q;
    }
}