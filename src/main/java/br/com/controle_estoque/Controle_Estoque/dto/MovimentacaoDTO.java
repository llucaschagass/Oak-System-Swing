package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;

/**
 * DTO para representar a entidade Movimentacao.
 * Usado para receber dados do histórico de movimentações da API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovimentacaoDTO {

    /** O identificador único da movimentação. */
    private long id;

    /** O produto que foi movimentado (DTO aninhado). */
    private ProdutoDTO produto;

    /** A data e hora exata em que a movimentação ocorreu. */
    private LocalDateTime dataMovimentacao;

    /** A quantidade de itens movimentados. */
    private int quantidadeMovimentada;

    /** O tipo da movimentação ("ENTRADA" ou "SAIDA"). */
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