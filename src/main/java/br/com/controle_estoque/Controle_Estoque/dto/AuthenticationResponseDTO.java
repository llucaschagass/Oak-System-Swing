package br.com.controle_estoque.Controle_Estoque.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * DTO que encapsula a resposta da API após uma autenticação bem-sucedida.
 * Contém o token JWT gerado.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationResponseDTO {

    /** O token JWT de acesso. */
    private String token;

    /**
     * Obtém o token JWT.
     * @return O token de acesso.
     */
    public String getToken() {
        return token;
    }

    /**
     * Define o token JWT.
     * @param token O token de acesso.
     */
    public void setToken(String token) {
        this.token = token;
    }
}