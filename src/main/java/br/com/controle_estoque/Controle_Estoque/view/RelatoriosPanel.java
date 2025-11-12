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

public class RelatoriosPanel extends JPanel {

    private ApiClient apiClient;
    private JPanel mainListPanel;
    private ObjectMapper objectMapper;

    public RelatoriosPanel(ApiClient apiClient) {
        this.apiClient = apiClient;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Relatórios Gerenciais");
        lblTitulo.setFont(UIManager.getFont("h1.font"));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        mainListPanel = new JPanel();
        mainListPanel.setLayout(new BoxLayout(mainListPanel, BoxLayout.Y_AXIS));
        mainListPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(mainListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        loadReportsData();
    }

    private void loadReportsData() {
        mainListPanel.removeAll();
        JLabel lblCarregando = new JLabel("Carregando relatórios, por favor aguarde...");
        lblCarregando.setFont(UIManager.getFont("h3.font"));
        mainListPanel.add(lblCarregando);
        mainListPanel.revalidate();
        mainListPanel.repaint();

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private BalancoGeralDTO balanco;
            private RelatorioMovimentacaoDTO maioresMovs;
            private List<ProdutoAbaixoMinimoDTO> abaixoMinimo;
            private List<ProdutosPorCategoriaDTO> porCategoria;
            private List<ListaPrecoDTO> listaPrecos;
            private String error = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
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
                    mainListPanel.add(createMaioresMovimentacoesPanel(maioresMovs));
                    mainListPanel.add(Box.createVerticalStrut(20));
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

    private JPanel createMaioresMovimentacoesPanel(RelatorioMovimentacaoDTO data) {
        JPanel panel = createReportSectionPanel("Maiores Movimentações");
        JPanel content = new JPanel(new GridLayout(1, 2, 10, 10));
        content.setBackground(Color.WHITE);

        JPanel cardEntrada = new JPanel(new BorderLayout());
        cardEntrada.setBorder(new EmptyBorder(10, 10, 10, 10));
        cardEntrada.setBackground(new Color(230, 255, 230));
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

        JPanel cardSaida = new JPanel(new BorderLayout());
        cardSaida.setBorder(new EmptyBorder(10, 10, 10, 10));
        cardSaida.setBackground(new Color(255, 230, 230));
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

    private JPanel createAbaixoMinimoPanel(List<ProdutoAbaixoMinimoDTO> data) {
        JPanel panel = createReportSectionPanel("Produtos Abaixo do Mínimo");
        String[] colunas = {"Produto", "Qtd. Mínima", "Qtd. Atual"};
        DefaultTableModel model = new DefaultTableModel(colunas, 0);
        if (data != null) {
            for (ProdutoAbaixoMinimoDTO item : data) {
                model.addRow(new Object[]{item.getNomeProduto(), item.getQuantidadeMinima(), item.getQuantidadeEmEstoque()});
            }
        }
        panel.add(createTable(model), BorderLayout.CENTER);
        return panel;
    }

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

    private JPanel createReportSectionPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0,0,0,50)),
                new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JLabel lblTitulo = new JLabel(title);
        lblTitulo.setFont(UIManager.getFont("h3.font"));
        lblTitulo.setBorder(new EmptyBorder(0, 0, 10, 0));
        panel.add(lblTitulo, BorderLayout.NORTH);
        return panel;
    }

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