package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MovimentacaoDTO {
    private long id;
    private ProdutoDTO produto;
    private LocalDateTime dataMovimentacao;
    private int quantidadeMovimentada;
    private String tipoMovimentacao;

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public ProdutoDTO getProduto() { return produto; }
    public void setProduto(ProdutoDTO produto) { this.produto = produto; }
    public LocalDateTime getDataMovimentacao() { return dataMovimentacao; }
    public void setDataMovimentacao(LocalDateTime data) { this.dataMovimentacao = data; }
    public int getQuantidadeMovimentada() { return quantidadeMovimentada; }
    public void setQuantidadeMovimentada(int q) { this.quantidadeMovimentada = q; }
    public String getTipoMovimentacao() { return tipoMovimentacao; }
    public void setTipoMovimentacao(String tipo) { this.tipoMovimentacao = tipo; }
}