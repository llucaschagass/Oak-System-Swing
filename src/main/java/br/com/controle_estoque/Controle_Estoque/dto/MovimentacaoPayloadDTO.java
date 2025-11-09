package br.com.controle_estoque.Controle_Estoque.dto;

class ProdutoIdPayload {
    private long id;
    public ProdutoIdPayload(long id) { this.id = id; }
    public long getId() { return id; }
}

public class MovimentacaoPayloadDTO {
    private ProdutoIdPayload produto;
    private int quantidadeMovimentada;
    private String tipoMovimentacao;

    public MovimentacaoPayloadDTO(long produtoId, int quantidade, String tipo) {
        this.produto = new ProdutoIdPayload(produtoId);
        this.quantidadeMovimentada = quantidade;
        this.tipoMovimentacao = tipo;
    }

    public ProdutoIdPayload getProduto() { return produto; }
    public int getQuantidadeMovimentada() { return quantidadeMovimentada; }
    public String getTipoMovimentacao() { return tipoMovimentacao; }
}