package br.com.controle_estoque.Controle_Estoque.auth;

/**
 * Gerenciador de sessão estático para a aplicação Swing.
 * Armazena o token JWT na memória após o login.
 */
public class AuthManager {

    /** O token JWT atualmente armazenado. */
    private static String jwtToken;

    /**
     * Define o token JWT após um login bem-sucedido.
     *
     * @param token O token JWT recebido da API.
     */
    public static void setToken(String token) {
        jwtToken = token;
        System.out.println("Token salvo com sucesso!");
    }

    /**
     * Obtém o token JWT armazenado.
     *
     * @return O token JWT, ou null se não estiver autenticado.
     */
    public static String getToken() {
        return jwtToken;
    }

    /**
     * Verifica se o usuário está atualmente autenticado.
     *
     * @return true se um token estiver armazenado, false caso contrário.
     */
    public static boolean isAuthenticated() {
        return jwtToken != null && !jwtToken.isEmpty();
    }

    /**
     * Limpa o token armazenado, efetivamente deslogando o usuário.
     */
    public static void logout() {
        jwtToken = null;
    }
}