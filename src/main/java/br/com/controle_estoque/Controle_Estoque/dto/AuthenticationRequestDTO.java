package br.com.controle_estoque.Controle_Estoque.dto;

public class AuthenticationRequestDTO {
    private String usuario;
    private String senha;

    public AuthenticationRequestDTO(String usuario, String senha) {
        this.usuario = usuario;
        this.senha = senha;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenha() {
        return senha;
    }
}