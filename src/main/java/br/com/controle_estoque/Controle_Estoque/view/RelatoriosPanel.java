package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Painel de exibição dos Relatórios Gerenciais.
 * Busca dados de 5 endpoints diferentes da API e os exibe em seções.
 */
public class RelatoriosPanel extends JPanel {

    /** Cliente para comunicação com a API. */
    private ApiClient apiClient;

    /** Painel principal que conterá todos os cards de relatório. */
    private JPanel mainListPanel;

    /** Conversor de JSON (não é mais necessário se o ApiClient já converte). */
    private ObjectMapper objectMapper;

    /**
     * Constrói o painel de relatórios.
     *
     * @param apiClient A instância do cliente de API.
     */
    public RelatoriosPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        // this.objectMapper = new ObjectMapper(); // Desnecessário se o ApiClient já retorna DTOs

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

        // --- Cabeçalho ---
        JLabel lblTitulo = new JLabel("Relatórios Gerenciais");
        lblTitulo.setFont(UIManager.getFont("h1.font"));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // --- Painel Principal ---
        mainListPanel = new JPanel();
        mainListPanel.setLayout(new BoxLayout(mainListPanel, BoxLayout.Y_AXIS)); // Layout vertical
        mainListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mainListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Melhora a velocidade do scroll
        add(scrollPane, BorderLayout.CENTER);

        // Carrega os dados
        loadReportsData();
    }

    /**
     * Busca os dados de todos os 5 relatórios da API em segundo plano (SwingWorker).
     * Atualiza a UI quando a busca termina.
     */
    private void loadReportsData() {
        mainListPanel.removeAll();
        JLabel lblCarregando = new JLabel("Carregando relatórios, por favor aguarde...");
        lblCarregando.setFont(UIManager.getFont("h3.font"));
        mainListPanel.add(lblCarregando);
        mainListPanel.revalidate();
        mainListPanel.repaint();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            // Variáveis para armazenar os resultados
            private BalancoGeralDTO balanco;
            private RelatorioMovimentacaoDTO maioresMovs;
            private List<ProdutoAbaixoMinimoDTO> abaixoMinimo;
            private List<ProdutosPorCategoriaDTO> porCategoria;
            private List<ListaPrecoDTO> listaPrecos;
            private String error = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    // Busca todos os relatórios (o ApiClient já retorna os DTOs)
                    balanco = apiClient.getBalancoFinanceiro();
                    maioresMovs = apiClient.getMaioresMovimentacoes();
                    abaixoMinimo = apiClient.getProdutosAbaixoMinimo();
                    porCategoria = apiClient.getProdutosPorCategoria();
                    listaPrecos = apiClient.getListaDePrecos();
                } catch (Exception e) {
                    error = e.getMessage();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                mainListPanel.removeAll();
                if (error != null) {
                    mainListPanel.add(new JLabel("Falha ao carregar relatórios: " + error));
                } else {
                    // Adiciona os painéis de relatório na ordem
                    mainListPanel.add(createMaioresMovimentacoesPanel(maioresMovs));
                    mainListPanel.add(Box.createVerticalStrut(20)); // Espaçador
                    mainListPanel.add(createBalancoPanel(balanco));
                    mainListPanel.add(Box.createVerticalStrut(20));
                    mainListPanel.add(createAbaixoMinimoPanel(abaixoMinimo));
                    mainListPanel.add(Box.createVerticalStrut(20));
                    mainListPanel.add(createPorCategoriaPanel(porCategoria));
                    mainListPanel.add(Box.createVerticalStrut(20));
                    mainListPanel.add(createListaPrecosPanel(listaPrecos));
                }
                mainListPanel.revalidate();
                mainListPanel.repaint();
            }
        };
        worker.execute();
    }

    // --- Métodos para criar cada card de relatório ---

    /**
     * Cria o painel para o relatório de Maiores Movimentações.
     * @param data O DTO com os dados do relatório.
     * @return Um JPanel formatado.
     */
    private JPanel createMaioresMovimentacoesPanel(RelatorioMovimentacaoDTO data) {
        JPanel panel = createReportSectionPanel("Maiores Movimentações");
        JPanel content = new JPanel(new GridLayout(1, 2, 10, 10));
        content.setBackground(Color.WHITE);

        // Card de Entradas
        JPanel cardEntrada = new JPanel(new BorderLayout());
        cardEntrada.setBorder(new EmptyBorder(10, 10, 10, 10));
        cardEntrada.setBackground(new Color(230, 255, 230)); // Fundo verde claro
        JLabel lblEntradaTitulo = new JLabel("Mais Entradas", SwingConstants.CENTER);
        lblEntradaTitulo.setFont(UIManager.getFont("h4.font"));
        String textoEntrada = "N/A";
        if (data != null && data.getProdutoComMaisEntradas() != null) {
            textoEntrada = data.getProdutoComMaisEntradas().getNomeProduto() + " (" + data.getProdutoComMaisEntradas().getTotalMovimentado() + ")";
        }
        JLabel lblEntradaValor = new JLabel(textoEntrada, SwingConstants.CENTER);
        lblEntradaValor.setFont(UIManager.getFont("h3.font"));
        cardEntrada.add(lblEntradaTitulo, BorderLayout.NORTH);
        cardEntrada.add(lblEntradaValor, BorderLayout.CENTER);

        // Card de Saídas
        JPanel cardSaida = new JPanel(new BorderLayout());
        cardSaida.setBorder(new EmptyBorder(10, 10, 10, 10));
        cardSaida.setBackground(new Color(255, 230, 230)); // Fundo vermelho claro
        JLabel lblSaidaTitulo = new JLabel("Mais Saídas", SwingConstants.CENTER);
        lblSaidaTitulo.setFont(UIManager.getFont("h4.font"));
        String textoSaida = "N/A";
        if (data != null && data.getProdutoComMaisSaidas() != null) {
            textoSaida = data.getProdutoComMaisSaidas().getNomeProduto() + " (" + data.getProdutoComMaisSaidas().getTotalMovimentado() + ")";
        }
        JLabel lblSaidaValor = new JLabel(textoSaida, SwingConstants.CENTER);
        lblSaidaValor.setFont(UIManager.getFont("h3.font"));
        cardSaida.add(lblSaidaTitulo, BorderLayout.NORTH);
        cardSaida.add(lblSaidaValor, BorderLayout.CENTER);

        content.add(cardEntrada);
        content.add(cardSaida);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Cria o painel para o relatório de Balanço Financeiro.
     * @param data O DTO com os dados do balanço.
     * @return Um JPanel formatado.
     */
    private JPanel createBalancoPanel(BalancoGeralDTO data) {
        JPanel panel = createReportSectionPanel("Balanço Físico/Financeiro");
        Locale br = new Locale("pt", "BR");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(br);

        String valorFormatado = (data != null) ? currencyFormat.format(data.getValorTotalEstoque()) : "R$ 0,00";
        JLabel lblTotal = new JLabel("Valor Total do Estoque: " + valorFormatado);

        lblTotal.setFont(UIManager.getFont("h3.font"));
        lblTotal.setBorder(new EmptyBorder(10, 5, 10, 5));
        panel.add(lblTotal, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Cria o painel para o relatório de Produtos Abaixo do Mínimo.
     * @param data A lista de produtos.
     * @return Um JPanel formatado com uma tabela.
     */
    private JPanel createAbaixoMinimoPanel(List<ProdutoAbaixoMinimoDTO> data) {
        JPanel panel = createReportSectionPanel("Produtos Abaixo do Mínimo");
        String[] colunas = {"Produto", "Qtd. Mínima", "Qtd. Atual"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        if (data != null && !data.isEmpty()) {
            for (ProdutoAbaixoMinimoDTO item : data) {
                model.addRow(new Object[]{item.getNomeProduto(), item.getQuantidadeMinima(), item.getQuantidadeEmEstoque()});
            }
        } else {
            model.addRow(new Object[]{"Nenhum produto abaixo do mínimo.", "", ""});
        }
        panel.add(createTable(model), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Cria o painel para o relatório de Produtos por Categoria.
     * @param data A lista de contagem por categoria.
     * @return Um JPanel formatado com uma tabela.
     */
    private JPanel createPorCategoriaPanel(List<ProdutosPorCategoriaDTO> data) {
        JPanel panel = createReportSectionPanel("Produtos por Categoria");
        String[] colunas = {"Categoria", "Qtd. de Produtos"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        if (data != null) {
            for (ProdutosPorCategoriaDTO item : data) {
                model.addRow(new Object[]{item.getNomeCategoria(), item.getQuantidadeProdutos()});
            }
        }
        panel.add(createTable(model), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Cria o painel para o relatório de Lista de Preços.
     * @param data A lista de preços.
     * @return Um JPanel formatado com uma tabela.
     */
    private JPanel createListaPrecosPanel(List<ListaPrecoDTO> data) {
        JPanel panel = createReportSectionPanel("Lista de Preços");
        String[] colunas = {"Produto", "Preço", "Unidade", "Categoria"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        Locale br = new Locale("pt", "BR");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(br);

        if (data != null) {
            for (ListaPrecoDTO item : data) {
                model.addRow(new Object[]{
                        item.getNomeProduto(),
                        currencyFormat.format(item.getPrecoUnitario()),
                        item.getUnidade(),
                        item.getNomeCategoria()
                });
            }
        }
        panel.add(createTable(model), BorderLayout.CENTER);
        return panel;
    }

    /**
     * Método auxiliar para criar um painel de seção padrão.
     * @param title O título da seção.
     * @return Um JPanel estilizado com borda e título.
     */
    private JPanel createReportSectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0,0,0,50)), // Sombra
                new EmptyBorder(15, 15, 15, 15) // Padding
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400)); // Limita a altura

        JLabel lblTitulo = new JLabel(title);
        lblTitulo.setFont(UIManager.getFont("h3.font"));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        return panel;
    }

    /**
     * Método auxiliar para criar uma JTable estilizada dentro de um JScrollPane.
     * @param model O modelo de dados da tabela.
     * @return Um JScrollPane contendo a JTable.
     */
    private JScrollPane createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(UIManager.getFont("Label.font"));
        table.setRowHeight(25);
        table.getTableHeader().setFont(UIManager.getFont("h4.font"));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }
}