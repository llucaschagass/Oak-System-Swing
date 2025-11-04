package br.com.controle_estoque.Controle_Estoque.auth;

public class AuthManager {

    private static String jwtToken;

    public static void setToken(String token) {
        jwtToken = token;
        System.out.println("Token salvo com sucesso!");
    }

    public static String getToken() {
        return jwtToken;
    }

    public static boolean isAuthenticated() {
        return jwtToken != null && !jwtToken.isEmpty();
    }

    public static void logout() {
        jwtToken = null;
    }
}