package br.com.controle_estoque.Controle_Estoque.dto;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProdutosPorCategoriaDTO {

    private String nomeCategoria;
    private long quantidadeProdutos;

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