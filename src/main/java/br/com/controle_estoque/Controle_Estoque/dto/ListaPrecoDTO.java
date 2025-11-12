package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

/**
 * DTO para representar os dados do Relatório de Lista de Preços.
 * Recebe dados do endpoint /api/relatorios/lista-de-precos.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListaPrecoDTO {

    /** O nome de exibição do produto. */
    private String nomeProduto;

    /** O preço de venda unitário do produto. */
    private BigDecimal precoUnitario;

    /** A unidade de medida do produto (ex: "UN", "KG"). */
    private String unidade;

    /** O nome da categoria à qual o produto pertence. */
    private String nomeCategoria;

    // Getters e Setters
    public String getNomeProduto() { return nomeProduto; }
    public void setNomeProduto(String nomeProduto) { this.nomeProduto = nomeProduto; }

    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }

    public String getUnidade() { return unidade; }
    public void setUnidade(String unidade) { this.unidade = unidade; }

    public String getNomeCategoria() { return nomeCategoria; }
    public void setNomeCategoria(String nomeCategoria) { this.nomeCategoria = nomeCategoria; }
}