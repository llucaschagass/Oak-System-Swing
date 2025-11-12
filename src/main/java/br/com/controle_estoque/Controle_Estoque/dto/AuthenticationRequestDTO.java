package br.com.controle_estoque.Controle_Estoque.dto;

/**
 * DTO para encapsular as credenciais de autenticação (login).
 * Usado como corpo da requisição POST para /api/auth/login.
 */
public class AuthenticationRequestDTO {

    /** O nome de usuário (login) do usuário. */
    private String usuario;

    /** A senha em texto puro do usuário. */
    private String senha;

    /**
     * Constrói o DTO de requisição de login.
     *
     * @param usuario O nome de usuário.
     * @param senha A senha.
     */
    public AuthenticationRequestDTO(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    /**
     * @return O nome de usuário.
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @return A senha.
     */
    public String getSenha() {
        return senha;
    }
}