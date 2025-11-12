package br.com.controle_estoque.Controle_Estoque.dto;

/**
 * DTO para o "payload" (carga de dados) ao registrar um novo usuário.
 * Usado como corpo da requisição POST para /api/auth/register.
 */
public class RegisterRequestDTO {
    private String nome;
    private String usuario;
    private String email;
    private String telefone;
    private String senha;

    /**
     * Constrói o DTO de requisição de registro.
     *
     * @param nome O nome completo do usuário.
     * @param usuario O nome de login (username).
     * @param email O email do usuário.
     * @param telefone O telefone do usuário.
     * @param senha A senha em texto puro.
     */
    public RegisterRequestDTO(String nome, String usuario, String email, String telefone, String senha) {
        this.nome = nome;
        this.usuario = usuario;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
    }

    // Getters são essenciais para o Jackson (conversor JSON)
    public String getNome() { return nome; }
    public String getUsuario() { return usuario; }
    public String getEmail() { return email; }
    public String getTelefone() { return telefone; }
    public String getSenha() { return senha; }
}