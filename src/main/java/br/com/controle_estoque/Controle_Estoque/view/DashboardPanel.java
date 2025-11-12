package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.BalancoGeralDTO;
import br.com.controle_estoque.Controle_Estoque.dto.MovimentacaoDTO;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutoAbaixoMinimoDTO;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutosPorCategoriaDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Painel de exibição do Dashboard principal.
 * Exibe os principais KPIs (Indicadores Chave de Desempenho) do sistema.
 */
public class DashboardPanel extends JPanel {

    /** Label para exibir o número total de produtos. */
    private JLabel lblValorTotalProdutos;

    /** Label para exibir o valor monetário total do estoque. */
    private JLabel lblValorTotalEstoque;

    /** Label para exibir a contagem de produtos abaixo do mínimo. */
    private JLabel lblValorAbaixoMinimo;

    /** Label para exibir o total de movimentações. */
    private JLabel lblValorMovimentacoes;

    /** Cliente para comunicação com a API. */
    private ApiClient apiClient;

    /** Conversor de JSON (usado se o ApiClient retornar String). */
    private ObjectMapper objectMapper;

    /**
     * Constrói o painel do Dashboard.
     *
     * @param apiClient A instância do cliente de API.
     */
    public DashboardPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        this.objectMapper = new ObjectMapper();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

        // --- Cabeçalho ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel lblTitulo = new JLabel("Dashboard");
        lblTitulo.setFont(UIManager.getFont("h1.font"));
        headerPanel.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblSubtitulo = new JLabel("Bem-vindo ao seu painel de controle de estoque!");
        lblSubtitulo.setFont(UIManager.getFont("large.font"));
        lblSubtitulo.setForeground(Color.GRAY);
        headerPanel.add(lblSubtitulo, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // --- Grid de KPIs ---
        JPanel kpiGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        kpiGridPanel.setBackground(Color.WHITE);

        lblValorTotalProdutos = createValorLabel();
        lblValorTotalEstoque = createValorLabel();
        lblValorAbaixoMinimo = createValorLabel();
        lblValorMovimentacoes = createValorLabel();

        kpiGridPanel.add(createKpiCard("Produtos Cadastrados", lblValorTotalProdutos, new Color(0, 150, 136)));
        kpiGridPanel.add(createKpiCard("Valor Total do Estoque", lblValorTotalEstoque, new Color(255, 152, 0)));
        kpiGridPanel.add(createKpiCard("Produtos Abaixo do Mínimo", lblValorAbaixoMinimo, new Color(244, 67, 54)));
        kpiGridPanel.add(createKpiCard("Total de Movimentações", lblValorMovimentacoes, new Color(33, 150, 243)));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(kpiGridPanel, BorderLayout.NORTH);

        add(contentPanel, BorderLayout.CENTER);

        loadDashboardData();
    }

    /**
     * Cria um JLabel padrão para os valores dos KPIs.
     * @return Um JLabel com o texto "Carregando...".
     */
    private JLabel createValorLabel() {
        JLabel label = new JLabel("Carregando...");
        label.setFont(UIManager.getFont("h1.font").deriveFont(32f));
        label.setForeground(Color.BLACK);
        return label;
    }

    /**
     * Cria um painel de card estilizado para um KPI.
     *
     * @param titulo O título do card.
     * @param lblValor O JLabel que exibirá o valor (para atualização futura).
     * @param corIndicador A cor da borda esquerda.
     * @return Um JPanel estilizado como um card.
     */
    private JPanel createKpiCard(String titulo, JLabel lblValor, Color corIndicador) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(250, 120));

        Border sombra = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0,0,0,50));
        Border indicador = BorderFactory.createMatteBorder(0, 5, 0, 0, corIndicador);
        Border padding = new EmptyBorder(15, 15, 15, 15);
        card.setBorder(BorderFactory.createCompoundBorder(sombra,
                BorderFactory.createCompoundBorder(indicador, padding)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(UIManager.getFont("h4.font"));
        lblTitulo.setForeground(Color.DARK_GRAY);
        card.add(lblTitulo, BorderLayout.NORTH);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }

    /**
     * Busca os dados dos 4 relatórios da API em segundo plano (SwingWorker)
     * e atualiza os JLabels dos cards quando a busca termina.
     */
    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            // Variáveis para armazenar os resultados
            private String totalProdutos = "Erro";
            private String valorEstoque = "Erro";
            private String abaixoMinimo = "Erro";
            private String totalMovs = "Erro";

            /**
             * Executa as chamadas de API em background.
             */
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Busca 1: Balanço Financeiro
                    BalancoGeralDTO balanco = apiClient.getBalancoFinanceiro();
                    Locale br = new Locale("pt", "BR");
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(br);
                    valorEstoque = currencyFormat.format(balanco.getValorTotalEstoque());

                    // Busca 2: Produtos Abaixo do Mínimo
                    List<ProdutoAbaixoMinimoDTO> listaAbaixo = apiClient.getProdutosAbaixoMinimo();
                    abaixoMinimo = String.valueOf(listaAbaixo.size());

                    // Busca 3: Total de Produtos
                    List<ProdutosPorCategoriaDTO> listaCat = apiClient.getProdutosPorCategoria();
                    long totalProd = 0;
                    for (ProdutosPorCategoriaDTO cat : listaCat) {
                        totalProd += cat.getQuantidadeProdutos();
                    }
                    totalProdutos = String.valueOf(totalProd);

                    // Busca 4: Total de Movimentações
                    List<MovimentacaoDTO> listaMovs = apiClient.getMovimentacoes();
                    totalMovs = String.valueOf(listaMovs.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            /**
             * Executa na thread da UI após o 'doInBackground' terminar.
             * Atualiza os JLabels com os valores encontrados.
             */
            @Override
            protected void done() {
                lblValorTotalProdutos.setText(totalProdutos);
                lblValorTotalEstoque.setText(valorEstoque);
                lblValorAbaixoMinimo.setText(abaixoMinimo);
                lblValorMovimentacoes.setText(totalMovs);
            }
        };
        worker.execute();
    }
}