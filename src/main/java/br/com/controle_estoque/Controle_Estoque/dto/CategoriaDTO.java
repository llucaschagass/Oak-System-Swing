package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO para representar a entidade Categoria.
 * Usado para transferir dados de categoria entre a API e o cliente.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CategoriaDTO {

    /** O identificador único da categoria. */
    private long id;

    /** O nome da categoria (ex: "Limpeza", "Enlatados"). */
    private String nome;

    /** O tamanho padrão dos itens desta categoria (ex: "Pequeno", "Médio"). */
    private String tamanho;

    /** O tipo de embalagem padrão (ex: "Lata", "Vidro"). */
    private String embalagem;

    // Getters e Setters
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getTamanho() { return tamanho; }
    public void setTamanho(String tamanho) { this.tamanho = tamanho; }

    public String getEmbalagem() { return embalagem; }
    public void setEmbalagem(String embalagem) { this.embalagem = embalagem; }

    /**
     * Retorna o nome da categoria.
     * Usado pelo JComboBox para exibir o texto correto no dropdown.
     * @return O nome da categoria.
     */
    @Override
    public String toString() {
        return nome;
    }
}