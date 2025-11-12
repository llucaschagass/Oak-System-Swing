package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.CategoriaDTO;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Painel de exibição e gerenciamento (CRUD) completo dos Produtos.
 * Este painel é exibido dentro da MainFrame.
 */
public class ProdutosPanel extends JPanel {

    /** Tabela Swing para exibir os produtos. */
    private JTable table;

    /** Modelo de dados da tabela, permite adicionar/remover linhas dinamicamente. */
    private DefaultTableModel tableModel;

    /** Cliente para comunicação com a API REST. */
    private ApiClient apiClient;

    /** Cache local da lista de produtos vinda da API. */
    private List<ProdutoDTO> listaProdutos;

    /** Cache local da lista de categorias vinda da API (usada no formulário de Adicionar/Editar). */
    private List<CategoriaDTO> listaCategorias;

    /**
     * Constrói o painel de gerenciamento de produtos.
     *
     * @param apiClient A instância do cliente de API (passada pela MainFrame).
     */
    public ProdutosPanel(ApiClient apiClient) {
        this.apiClient = apiClient;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

        // --- 1. Cabeçalho (Título e Botões de Ação) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitulo = new JLabel("Gerenciar Produtos");
        lblTitulo.setFont(UIManager.getFont("h1.font"));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        // Painel para os botões do cabeçalho (Adicionar, Reajustar)
        JPanel headerButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        headerButtonsPanel.setBackground(Color.WHITE);

        JButton btnReajustar = new JButton("Reajustar Preços");
        btnReajustar.setFont(UIManager.getFont("Label.font").deriveFont(Font.BOLD));
        btnReajustar.setBackground(new Color(230, 230, 230));
        btnReajustar.setForeground(Color.BLACK);
        btnReajustar.setFocusPainted(false);
        btnReajustar.addActionListener(e -> onReajustarPrecos());

        JButton btnAdicionar = new JButton("+ Adicionar Produto");
        btnAdicionar.setFont(UIManager.getFont("h4.font"));
        btnAdicionar.setBackground(new Color(0x650f0f));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.addActionListener(e -> onAddProduto());

        headerButtonsPanel.add(btnReajustar);
        headerButtonsPanel.add(btnAdicionar);
        headerPanel.add(headerButtonsPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // --- 2. Tabela de Produtos ---
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

        // --- 3. Painel de Ações ---
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(Color.WHITE);

        JButton btnEditar = new JButton("Editar Selecionado");
        btnEditar.addActionListener(e -> onEditProduto());
        actionsPanel.add(btnEditar);

        JButton btnExcluir = new JButton("Excluir Selecionado");
        btnExcluir.addActionListener(e -> onDeleteProduto());
        actionsPanel.add(btnExcluir);

        add(actionsPanel, BorderLayout.SOUTH);

        // --- 4. Carregamento Inicial ---
        loadInitialData();
    }

    /**
     * Busca os dados iniciais (produtos e categorias) da API em segundo plano.
     * Usa um {@link SwingWorker} para não travar a UI durante a chamada de rede.
     */
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
                    // Busca produtos e categorias em paralelo
                    produtos = apiClient.getProdutos();
                    categorias = apiClient.getCategorias();
                } catch (Exception e) {
                    error = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                tableModel.setRowCount(0); // Limpa o "Carregando..."
                if (error != null) {
                    tableModel.addRow(new Object[]{"Falha ao carregar: " + error, "", "", "", "", ""});
                } else {
                    listaProdutos = produtos; // Salva no cache local
                    listaCategorias = categorias; // Salva no cache local
                    preencherTabela(); // Popula a tabela
                }
            }
        };
        worker.execute();
    }

    /**
     * Preenche a {@link JTable} com os dados da lista de produtos local.
     * Formata o preço para a moeda local (BRL).
     */
    private void preencherTabela() {
        tableModel.setRowCount(0);
        Locale br = new Locale("pt", "BR");
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(br);

        if (listaProdutos == null) return;

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

    /**
     * Ação disparada pelo botão "Adicionar Produto".
     * Abre o {@link ProdutoFormDialog} em modo de criação (passando {@code null}).
     */
    private void onAddProduto() {
        if (listaCategorias == null) {
            JOptionPane.showMessageDialog(this, "Categorias ainda carregando. Tente novamente.", "Aguarde", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ProdutoFormDialog dialog = new ProdutoFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                apiClient,
                listaCategorias,
                null, // 'null' para produto indica modo de ADIÇÃO
                this::loadInitialData // Callback para recarregar a tabela
        );
        dialog.setVisible(true);
    }

    /**
     * Ação disparada pelo botão "Editar Selecionado".
     * Abre o {@link ProdutoFormDialog} em modo de edição com o produto selecionado.
     */
    private void onEditProduto() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto na tabela para editar.", "Nenhum Produto Selecionado", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Pega o produto do cache local baseado na linha
        ProdutoDTO produtoSelecionado = listaProdutos.get(selectedRow);

        ProdutoFormDialog dialog = new ProdutoFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                apiClient,
                listaCategorias,
                produtoSelecionado, // Passa o produto para o modo de EDIÇÃO
                this::loadInitialData
        );
        dialog.setVisible(true);
    }

    /**
     * Ação disparada pelo botão "Excluir Selecionado".
     * Pede confirmação e deleta o produto selecionado via API.
     */
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
                        get(); // Verifica se houve erro
                        JOptionPane.showMessageDialog(ProdutosPanel.this, "Produto excluído com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                        loadInitialData(); // Recarrega a tabela
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ProdutosPanel.this, "Erro ao excluir produto: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    /**
     * Ação disparada pelo botão "Reajustar Preços".
     * Pede um percentual ao usuário e chama a API para o reajuste em massa.
     */
    private void onReajustarPrecos() {
        String input = JOptionPane.showInputDialog(
                this,
                "Digite o percentual de reajuste (ex: 10 para aumento, -5 para desconto):",
                "Reajuste de Preço em Massa",
                JOptionPane.PLAIN_MESSAGE
        );

        if (input == null || input.trim().isEmpty()) {
            return; // Usuário cancelou
        }

        try {
            // Permite que o usuário digite "10,5" ou "10.5"
            BigDecimal percentual = new BigDecimal(input.trim().replace(",", "."));

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Tem certeza que deseja aplicar um reajuste de " + percentual + "% em TODOS os produtos?",
                    "Confirmação",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Pega o botão para desabilitá-lo durante a operação
            JButton btnReajustar = (JButton) ((JPanel) ((JPanel) getComponent(0)).getComponent(1)).getComponent(0);
            btnReajustar.setText("Reajustando...");
            btnReajustar.setEnabled(false);

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    apiClient.reajustarPrecos(percentual);
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Verifica se houve erro
                        JOptionPane.showMessageDialog(ProdutosPanel.this, "Preços reajustados com sucesso!");
                        loadInitialData(); // Recarrega a tabela para mostrar os novos preços
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ProdutosPanel.this, "Erro ao reajustar preços: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    } finally {
                        btnReajustar.setText("Reajustar Preços");
                        btnReajustar.setEnabled(true);
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Valor inválido. Por favor, insira apenas números (ex: 10 ou -5.5).", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}