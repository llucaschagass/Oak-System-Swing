package br.com.controle_estoque.Controle_Estoque.dto;

/**
 * Classe auxiliar interna para encapsular o ID de um Produto.
 * Usada para criar o JSON aninhado {"id": 123} no payload.
 */
class ProdutoIdPayload {
    private long id;
    public ProdutoIdPayload(long id) { this.id = id; }
    public long getId() { return id; }
}

/**
 * DTO para o "payload" (carga de dados) ao criar uma nova Movimentação.
 * Usado como corpo da requisição POST para /api/movimentacoes.
 */
public class MovimentacaoPayloadDTO {

    /** O produto a ser movimentado, representado por seu ID. */
    private ProdutoIdPayload produto;

    /** A quantidade de itens a ser movimentada. */
    private int quantidadeMovimentada;

    /** O tipo da movimentação ("ENTRADA" ou "SAIDA"). */
    private String tipoMovimentacao;

    /**
     * Constrói o DTO de payload da movimentação.
     *
     * @param produtoId O ID do produto.
     * @param quantidade A quantidade movimentada.
     * @param tipo O tipo ("ENTRADA" ou "SAIDA").
     */
    public MovimentacaoPayloadDTO(long produtoId, int quantidade, String tipo) {
        this.produto = new ProdutoIdPayload(produtoId);
        this.quantidadeMovimentada = quantidade;
        this.tipoMovimentacao = tipo;
    }

    /**
     * @return O objeto aninhado do produto com ID.
     */
    public ProdutoIdPayload getProduto() { return produto; }

    /**
     * @return A quantidade movimentada.
     */
    public int getQuantidadeMovimentada() { return quantidadeMovimentada; }

    /**
     * @return O tipo da movimentação.
     */
    public String getTipoMovimentacao() { return tipoMovimentacao; }
}