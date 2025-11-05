package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.BalancoGeralDTO;
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

public class DashboardPanel extends JPanel {

    private JLabel lblValorTotalProdutos;
    private JLabel lblValorTotalEstoque;
    private JLabel lblValorAbaixoMinimo;
    private JLabel lblValorMovimentacoes;
    private ApiClient apiClient;
    private ObjectMapper objectMapper;

    public DashboardPanel(ApiClient apiClient) {
        this.apiClient = new ApiClient();
        this.objectMapper = new ObjectMapper();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

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

    private JLabel createValorLabel() {
        JLabel label = new JLabel("Carregando...");
        label.setFont(UIManager.getFont("h1.font").deriveFont(32f));
        label.setForeground(Color.BLACK);
        return label;
    }

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
     * Busca os dados da API
     */
    private void loadDashboardData() {
        SwingWorker<Void, Void> worker = new SwingWorker<>() {

            private String totalProdutos = "Erro";
            private String valorEstoque = "Erro";
            private String abaixoMinimo = "Erro";
            private String totalMovs = "Erro";

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String balancoJson = apiClient.getBalancoFinanceiro();
                    BalancoGeralDTO balanco = objectMapper.readValue(balancoJson, BalancoGeralDTO.class);
                    Locale br = new Locale("pt", "BR");
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(br);
                    valorEstoque = currencyFormat.format(balanco.getValorTotalEstoque());

                    String abaixoMinimoJson = apiClient.getProdutosAbaixoMinimo();
                    List<Object> listaAbaixo = objectMapper.readValue(abaixoMinimoJson, new TypeReference<>() {});
                    abaixoMinimo = String.valueOf(listaAbaixo.size());

                    String porCategoriaJson = apiClient.getProdutosPorCategoria();
                    List<ProdutosPorCategoriaDTO> listaCat = objectMapper.readValue(porCategoriaJson, new TypeReference<>() {});
                    long totalProd = 0;
                    for (ProdutosPorCategoriaDTO cat : listaCat) {
                        totalProd += cat.getQuantidadeProdutos();
                    }
                    totalProdutos = String.valueOf(totalProd);
                    String movsJson = apiClient.getMovimentacoes();
                    List<Object> listaMovs = objectMapper.readValue(movsJson, new TypeReference<>() {});
                    totalMovs = String.valueOf(listaMovs.size());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

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