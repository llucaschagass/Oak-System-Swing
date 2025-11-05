package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.CategoriaDTO;
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

    private List<ProdutoDTO> listaProdutos;
    private List<CategoriaDTO> listaCategorias;

    public ProdutosPanel(ApiClient apiClient) {
        this.apiClient = apiClient;

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
        btnAdicionar.addActionListener(e -> onAddProduto());
        headerPanel.add(btnAdicionar, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Preço Unitário", "Qtd. Estoque", "Unidade", "Categoria"};

        tableModel = new DefaultTableModel(colunas, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        table = new JTable(tableModel);

        table.setFont(UIManager.getFont("Label.font"));
        table.setRowHeight(30);
        table.getTableHeader().setFont(UIManager.getFont("h4.font"));
        table.getTableHeader().setBackground(Color.LIGHT_GRAY);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(Color.WHITE);

        JButton btnEditar = new JButton("Editar Selecionado");
        btnEditar.addActionListener(e -> onEditProduto());
        actionsPanel.add(btnEditar);

        JButton btnExcluir = new JButton("Excluir Selecionado");
        btnExcluir.addActionListener(e -> onDeleteProduto());
        actionsPanel.add(btnExcluir);

        add(actionsPanel, BorderLayout.SOUTH);

        loadInitialData();
    }

    private void loadInitialData() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"Carregando dados...", "", "", "", "", ""});

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<ProdutoDTO> produtos;
            private List<CategoriaDTO> categorias;
            private String error = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    produtos = apiClient.getProdutos();
                    categorias = apiClient.getCategorias();
                } catch (Exception e) {
                    error = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                tableModel.setRowCount(0);
                if (error != null) {
                    tableModel.addRow(new Object[]{"Falha ao carregar: " + error, "", "", "", "", ""});
                } else {
                    listaProdutos = produtos;
                    listaCategorias = categorias;
                    preencherTabela();
                }
            }
        };
        worker.execute();
    }

    private void preencherTabela() {
        tableModel.setRowCount(0);
        Locale br = new Locale("pt", "BR");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(br);

        for (ProdutoDTO produto : listaProdutos) {
            tableModel.addRow(new Object[]{
                    produto.getId(),
                    produto.getNome(),
                    currencyFormat.format(produto.getPrecoUnitario()),
                    produto.getQuantidadeEmEstoque(),
                    produto.getUnidade(),
                    produto.getCategoria().getNome()
            });
        }
    }

    private void onAddProduto() {
        if (listaCategorias == null) {
            JOptionPane.showMessageDialog(this, "Categorias ainda carregando. Tente novamente.", "Aguarde", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProdutoFormDialog dialog = new ProdutoFormDialog((Frame) SwingUtilities.getWindowAncestor(this), apiClient, listaCategorias, null, this::loadInitialData);
        dialog.setVisible(true);
    }

    private void onEditProduto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto na tabela para editar.", "Nenhum Produto Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProdutoDTO produtoSelecionado = listaProdutos.get(selectedRow);
        ProdutoFormDialog dialog = new ProdutoFormDialog((Frame) SwingUtilities.getWindowAncestor(this), apiClient, listaCategorias, produtoSelecionado, this::loadInitialData);
        dialog.setVisible(true);
    }

    private void onDeleteProduto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto na tabela para excluir.", "Nenhum Produto Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProdutoDTO produtoSelecionado = listaProdutos.get(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir o produto: " + produtoSelecionado.getNome() + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    apiClient.deleteProduto(produtoSelecionado.getId());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(ProdutosPanel.this, "Produto excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        loadInitialData();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ProdutosPanel.this, "Erro ao excluir produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}