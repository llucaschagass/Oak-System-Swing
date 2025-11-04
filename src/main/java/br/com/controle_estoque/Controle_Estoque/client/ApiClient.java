package br.com.controle_estoque.Controle_Estoque.client;

import br.com.controle_estoque.Controle_Estoque.dto.AuthenticationRequestDTO;
import br.com.controle_estoque.Controle_Estoque.dto.AuthenticationResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "http://localhost:8080";

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Tenta autenticar o usuário na API.
     */
    public AuthenticationResponseDTO login(String usuario, String senha) throws Exception {
        AuthenticationRequestDTO requestDTO = new AuthenticationRequestDTO(usuario, senha);

        String requestBody = objectMapper.writeValueAsString(requestDTO);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha no login. Status: " + response.statusCode());
        }

        return objectMapper.readValue(response.body(), AuthenticationResponseDTO.class);
    }
}