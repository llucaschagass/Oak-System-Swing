package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RelatorioMovimentacaoDTO {
    private ProdutoMovimentacaoDTO produtoComMaisSaidas;
    private ProdutoMovimentacaoDTO produtoComMaisEntradas;

    // Getters e Setters
    public ProdutoMovimentacaoDTO getProdutoComMaisSaidas() { return produtoComMaisSaidas; }
    public void setProdutoComMaisSaidas(ProdutoMovimentacaoDTO p) { this.produtoComMaisSaidas = p; }
    public ProdutoMovimentacaoDTO getProdutoComMaisEntradas() { return produtoComMaisEntradas; }
    public void setProdutoComMaisEntradas(ProdutoMovimentacaoDTO p) { this.produtoComMaisEntradas = p; }
}