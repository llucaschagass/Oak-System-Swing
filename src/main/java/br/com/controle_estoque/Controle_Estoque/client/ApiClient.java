package br.com.controle_estoque.Controle_Estoque.client;

import br.com.controle_estoque.Controle_Estoque.auth.AuthManager;
import br.com.controle_estoque.Controle_Estoque.dto.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

/**
 * Classe central para a comunicação com a API REST do Controle de Estoque.
 * Encapsula todas as chamadas HTTP (GET, POST, PUT, DELETE) e a
 * serialização/desserialização de JSON.
 */
public class ApiClient {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    /** URL base da API Spring Boot. */
    private static final String BASE_URL = "http://localhost:8080";

    /**
     * Constrói um novo ApiClient.
     * Inicializa o HttpClient e o ObjectMapper, registrando o módulo de datas (JavaTimeModule)
     * para lidar corretamente com {@link java.time.LocalDateTime}.
     */
    public ApiClient() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Autentica o usuário na API e retorna um token JWT.
     *
     * @param usuario O nome de usuário para login.
     * @param senha A senha do usuário.
     * @return Um {@link AuthenticationResponseDTO} contendo o token JWT.
     * @throws Exception Se a autenticação falhar (ex: 403, 500) ou houver erro de rede.
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
     * Registra um novo usuário na API e retorna um token de autenticação.
     * Assume que o back-end faz o login automaticamente após o registro.
     *
     * @param registerRequest DTO contendo os dados do novo usuário.
     * @return Um {@link AuthenticationResponseDTO} contendo o token JWT.
     * @throws Exception Se o registro falhar (ex: usuário ou email já existe).
     */
    public AuthenticationResponseDTO register(RegisterRequestDTO registerRequest) throws Exception {
        // Converte o objeto Java para JSON
        String requestBody = objectMapper.writeValueAsString(registerRequest);

        // Monta a requisição POST
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        // Envia a requisição
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        // O back-end retorna 200 OK no registro (assim como no login)
        if (response.statusCode() != 200) {
            // Tenta extrair a mensagem de erro do back-end
            String erroMsg = response.body();
            if (erroMsg.startsWith("\"") && erroMsg.endsWith("\"")) {
                erroMsg = erroMsg.substring(1, erroMsg.length() - 1);
            }
            throw new RuntimeException("Falha no registro: " + erroMsg);
        }

        // Converte a resposta JSON (com o token) para o DTO
        return objectMapper.readValue(response.body(), AuthenticationResponseDTO.class);
    }

    /**
     * Método auxiliar genérico para executar requisições GET autenticadas.
     * Adiciona automaticamente o token JWT salvo no {@link AuthManager}.
     * Também lida com erros de token expirado (401/403), realizando o logout.
     *
     * @param endpoint O caminho da API (ex: "/api/produtos").
     * @return A resposta da API como uma String JSON.
     * @throws Exception Se o usuário não estiver autenticado, o token for inválido, ou houver erro de rede.
     */
    private String sendGetRequest(String endpoint) throws Exception {
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

    // --- MÉTODOS DE RELATÓRIO ---

    /**
     * Busca os dados do relatório de Balanço Financeiro.
     *
     * @return Um objeto {@link BalancoGeralDTO} com os dados do balanço.
     * @throws Exception Se a requisição falhar.
     */
    public BalancoGeralDTO getBalancoFinanceiro() throws Exception {
        String jsonResponse = sendGetRequest("/api/relatorios/balanco-financeiro");
        return objectMapper.readValue(jsonResponse, BalancoGeralDTO.class);
    }

    /**
     * Busca o relatório de produtos abaixo do estoque mínimo.
     *
     * @return Uma lista de {@link ProdutoAbaixoMinimoDTO}.
     * @throws Exception Se a requisição falhar.
     */
    public List<ProdutoAbaixoMinimoDTO> getProdutosAbaixoMinimo() throws Exception {
        String jsonResponse = sendGetRequest("/api/relatorios/produtos-abaixo-minimo");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<ProdutoAbaixoMinimoDTO>>() {});
    }

    /**
     * Busca o relatório de contagem de produtos por categoria.
     *
     * @return Uma lista de {@link ProdutosPorCategoriaDTO}.
     * @throws Exception Se a requisição falhar.
     */
    public List<ProdutosPorCategoriaDTO> getProdutosPorCategoria() throws Exception {
        String jsonResponse = sendGetRequest("/api/relatorios/produtos-por-categoria");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<ProdutosPorCategoriaDTO>>() {});
    }

    /**
     * Busca o histórico completo de movimentações.
     *
     * @return Uma lista de {@link MovimentacaoDTO}.
     * @throws Exception Se a requisição falhar.
     */
    public List<MovimentacaoDTO> getMovimentacoes() throws Exception {
        String jsonResponse = sendGetRequest("/api/movimentacoes");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<MovimentacaoDTO>>() {});
    }

    /**
     * Busca o relatório de produtos com maiores entradas e saídas.
     *
     * @return Um objeto {@link RelatorioMovimentacaoDTO}.
     * @throws Exception Se a requisição falhar.
     */
    public RelatorioMovimentacaoDTO getMaioresMovimentacoes() throws Exception {
        String jsonResponse = sendGetRequest("/api/relatorios/maiores-movimentacoes");
        return objectMapper.readValue(jsonResponse, RelatorioMovimentacaoDTO.class);
    }

    /**
     * Busca o relatório de lista de preços.
     *
     * @return Uma lista de {@link ListaPrecoDTO}.
     * @throws Exception Se a requisição falhar.
     */
    public List<ListaPrecoDTO> getListaDePrecos() throws Exception {
        String jsonResponse = sendGetRequest("/api/relatorios/lista-de-precos");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<ListaPrecoDTO>>() {});
    }

    // --- CRUD PRODUTOS ---

    /**
     * Busca a lista completa de produtos.
     *
     * @return Uma lista de {@link ProdutoDTO}.
     * @throws Exception Se a requisição falhar.
     */
    public List<ProdutoDTO> getProdutos() throws Exception {
        String jsonResponse = sendGetRequest("/api/produtos");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<ProdutoDTO>>() {});
    }

    /**
     * Envia um novo produto para ser criado na API.
     *
     * @param produto Um {@link ProdutoPayloadDTO} com os dados do novo produto.
     * @return O {@link ProdutoDTO} do produto recém-criado (com ID).
     * @throws Exception Se a criação falhar (ex: 400 Bad Request, 500).
     */
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

    /**
     * Envia dados para atualizar um produto existente.
     *
     * @param id O ID do produto a ser atualizado.
     * @param produto Um {@link ProdutoPayloadDTO} com os novos dados.
     * @return O {@link ProdutoDTO} do produto atualizado.
     * @throws Exception Se a atualização falhar.
     */
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

    /**
     * Deleta um produto da API.
     *
     * @param id O ID do produto a ser deletado.
     * @throws Exception Se a deleção falhar (ex: 404 Not Found).
     */
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

    // --- CRUD CATEGORIAS ---

    /**
     * Busca a lista completa de categorias.
     *
     * @return Uma lista de {@link CategoriaDTO}.
     * @throws Exception Se a requisição falhar.
     */
    public List<CategoriaDTO> getCategorias() throws Exception {
        String jsonResponse = sendGetRequest("/api/categorias");
        return objectMapper.readValue(jsonResponse, new TypeReference<List<CategoriaDTO>>() {});
    }

    /**
     * Envia uma nova categoria para ser criada na API.
     *
     * @param categoria Um {@link CategoriaDTO} com os dados da nova categoria.
     * @return A {@link CategoriaDTO} recém-criada (com ID).
     * @throws Exception Se a criação falhar.
     */
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

    /**
     * Envia dados para atualizar uma categoria existente.
     *
     * @param id O ID da categoria a ser atualizada.
     * @param categoria Um {@link CategoriaDTO} com os novos dados.
     * @return A {@link CategoriaDTO} atualizada.
     * @throws Exception Se a atualização falhar.
     */
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

    /**
     * Deleta uma categoria da API.
     *
     * @param id O ID da categoria a ser deletada.
     * @throws Exception Se a deleção falhar.
     */
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

    // --- CRUD MOVIMENTAÇÕES ---

    /**
     * Cria uma nova movimentação de estoque (entrada ou saída).
     *
     * @param movimentacao Um {@link MovimentacaoPayloadDTO} com os dados da movimentação.
     * @return A {@link MovimentacaoDTO} recém-criada.
     * @throws Exception Se a criação falhar (ex: estoque insuficiente).
     */
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
            // Remove aspas da mensagem de erro da API (ex: "Estoque insuficiente")
            if (erroMsg.startsWith("\"") && erroMsg.endsWith("\"")) {
                erroMsg = erroMsg.substring(1, erroMsg.length() - 1);
            }
            throw new RuntimeException(erroMsg);
        }
    }

    // --- REAJUSTE DE PREÇO ---

    /**
     * Envia uma requisição para reajustar o preço de todos os produtos em massa.
     *
     * @param percentual O percentual de reajuste (ex: 10.0 para +10%, -5.0 para -5%).
     * @throws Exception Se a operação falhar.
     */
    public void reajustarPrecos(BigDecimal percentual) throws Exception {
        ReajustePrecoDTO payload = new ReajustePrecoDTO(percentual);
        String requestBody = objectMapper.writeValueAsString(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/produtos/reajustar-preco"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + AuthManager.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha ao reajustar preços. Status: " + response.statusCode());
        }
    }
}