package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProdutosPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private ApiClient apiClient;

    public ProdutosPanel(ApiClient apiClient) {
        this.apiClient = new ApiClient();

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitulo = new JLabel("Gerenciar Produtos");
        lblTitulo.setFont(UIManager.getFont("h1.font"));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JButton btnAdicionar = new JButton("+ Adicionar Produto");
        btnAdicionar.setFont(UIManager.getFont("h4.font"));
        btnAdicionar.setBackground(new Color(0x650f0f));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setFocusPainted(false);
        headerPanel.add(btnAdicionar, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        String[] colunas = {"ID", "Nome", "Preço Unitário", "Qtd. Estoque", "Unidade", "Categoria"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);

        table.setFont(UIManager.getFont("Label.font"));
        table.setRowHeight(30);
        table.getTableHeader().setFont(UIManager.getFont("h4.font"));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

        loadProdutosData();
    }

    private void loadProdutosData() {
        tableModel.addRow(new Object[]{"Carregando...", "", "", "", "", ""});

        SwingWorker<List<ProdutoDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<ProdutoDTO> doInBackground() throws Exception {
                return apiClient.getProdutos();
            }

            @Override
            protected void done() {
                tableModel.setRowCount(0);

                try {
                    List<ProdutoDTO> produtos = get();
                    Locale br = new Locale("pt", "BR");
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(br);

                    for (ProdutoDTO produto : produtos) {
                        tableModel.addRow(new Object[]{
                                produto.getId(),
                                produto.getNome(),
                                currencyFormat.format(produto.getPrecoUnitario()),
                                produto.getQuantidadeEmEstoque(),
                                produto.getUnidade(),
                                produto.getCategoria().getNome()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    tableModel.addRow(new Object[]{"Falha ao carregar dados.", "", "", "", "", ""});
                    JOptionPane.showMessageDialog(ProdutosPanel.this,
                            "Falha ao carregar produtos: " + e.getMessage(),
                            "Erro de API",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}