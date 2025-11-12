package br.com.controle_estoque.Controle_Estoque.dto;

/**
 * DTO auxiliar para encapsular o ID de uma Categoria.
 * Usado para criar payloads JSON aninhados, como {"id": 1}.
 */
public class CategoriaIdDTO {

    /** O identificador único da categoria. */
    private long id;

    /**
     * Constrói o DTO de ID da categoria.
     *
     * @param id O ID da categoria.
     */
    public CategoriaIdDTO(long id) {
        this.id = id;
    }

    /**
     * @return O ID da categoria.
     */
    public long getId() {
        return id;
    }
}