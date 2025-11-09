package br.com.controle_estoque.Controle_Estoque.client;

import br.com.controle_estoque.Controle_Estoque.auth.AuthManager;
import br.com.controle_estoque.Controle_Estoque.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final String BASE_URL = "http://localhost:8080";

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
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

    /**
     * Método genérico para fazer requisições GET autenticadas.
     * @param endpoint O endpoint da API
     * @return A resposta da API
     */
    private String sendGetRequest(String endpoint) throws Exception {
        // Verifica se tem token salvo
        if (!AuthManager.isAuthenticated()) {
            throw new RuntimeException("Usuário não autenticado. Faça o login novamente.");
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + endpoint))
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 401 || response.statusCode() == 403) {
            AuthManager.logout();
            throw new RuntimeException("Sessão expirada. Faça o login novamente.");
        }
        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao buscar dados: " + endpoint + ". Status: " + response.statusCode());
        }

        return response.body();
    }

    public String getBalancoFinanceiro() throws Exception {
        return sendGetRequest("/api/relatorios/balanco-financeiro");
    }

    public String getProdutosAbaixoMinimo() throws Exception {
        return sendGetRequest("/api/relatorios/produtos-abaixo-minimo");
    }

    public String getProdutosPorCategoria() throws Exception {
        return sendGetRequest("/api/relatorios/produtos-por-categoria");
    }

    public List<MovimentacaoDTO> getMovimentacoes() throws Exception {
        String jsonResponse = sendGetRequest("/api/movimentacoes");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<MovimentacaoDTO>>() {});
    }

    // CRUD Produtos
    public List<ProdutoDTO> getProdutos() throws Exception {
        String jsonResponse = sendGetRequest("/api/produtos");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<ProdutoDTO>>() {});
    }

    public ProdutoDTO createProduto(ProdutoPayloadDTO produto) throws Exception {
        String requestBody = objectMapper.writeValueAsString(produto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/produtos"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 201) {
            throw new RuntimeException("Falha ao criar produto. Status: " + response.statusCode());
        }
        return objectMapper.readValue(response.body(), ProdutoDTO.class);
    }

    public ProdutoDTO updateProduto(long id, ProdutoPayloadDTO produto) throws Exception {
        String requestBody = objectMapper.writeValueAsString(produto);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/produtos/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao atualizar produto. Status: " + response.statusCode());
        }
        return objectMapper.readValue(response.body(), ProdutoDTO.class);
    }

    public void deleteProduto(long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/produtos/" + id))
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .DELETE()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 204) {
            throw new RuntimeException("Falha ao deletar produto. Status: " + response.statusCode());
        }
    }

    // CRUD Categorias
    public List<CategoriaDTO> getCategorias() throws Exception {
        String jsonResponse = sendGetRequest("/api/categorias");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<CategoriaDTO>>() {});
    }

    public CategoriaDTO createCategoria(CategoriaDTO categoria) throws Exception {
        String requestBody = objectMapper.writeValueAsString(categoria);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/categorias"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 201) {
            throw new RuntimeException("Falha ao criar categoria: " + response.body());
        }
        return objectMapper.readValue(response.body(), CategoriaDTO.class);
    }

    public CategoriaDTO updateCategoria(long id, CategoriaDTO categoria) throws Exception {
        String requestBody = objectMapper.writeValueAsString(categoria);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/categorias/" + id))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao atualizar categoria: " + response.body());
        }
        return objectMapper.readValue(response.body(), CategoriaDTO.class);
    }

    public void deleteCategoria(long id) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/categorias/" + id))
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .DELETE()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 204) {
            throw new RuntimeException("Falha ao deletar categoria: " + response.body());
        }
    }

    public MovimentacaoDTO createMovimentacao(MovimentacaoPayloadDTO movimentacao) throws Exception {
        String requestBody = objectMapper.writeValueAsString(movimentacao);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/movimentacoes"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201) {
            return objectMapper.readValue(response.body(), MovimentacaoDTO.class);
        } else {
            String erroMsg = response.body();
            if (erroMsg.startsWith("\"") && erroMsg.endsWith("\"")) {
                erroMsg = erroMsg.substring(1, erroMsg.length() - 1);
            }
            throw new RuntimeException(erroMsg);
        }
    }
}