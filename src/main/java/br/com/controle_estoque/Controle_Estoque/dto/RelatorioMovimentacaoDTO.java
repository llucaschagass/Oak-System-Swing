package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para representar os dados do Relatório de Maiores Movimentações.
 * Recebe dados do endpoint /api/relatorios/maiores-movimentacoes.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatorioMovimentacaoDTO {

    /** O produto com o maior somatório de saídas. */
    private ProdutoMovimentacaoDTO produtoComMaisSaidas;

    /** O produto com o maior somatório de entradas. */
    private ProdutoMovimentacaoDTO produtoComMaisEntradas;

    // Getters e Setters
    public ProdutoMovimentacaoDTO getProdutoComMaisSaidas() { return produtoComMaisSaidas; }
    public void setProdutoComMaisSaidas(ProdutoMovimentacaoDTO p) { this.produtoComMaisSaidas = p; }

    public ProdutoMovimentacaoDTO getProdutoComMaisEntradas() { return produtoComMaisEntradas; }
    public void setProdutoComMaisEntradas(ProdutoMovimentacaoDTO p) { this.produtoComMaisEntradas = p; }
}