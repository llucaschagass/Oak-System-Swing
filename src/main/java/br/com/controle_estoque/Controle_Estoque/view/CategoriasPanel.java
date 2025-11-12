package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.CategoriaDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Painel de exibição e gerenciamento (CRUD) das Categorias.
 * Este painel é exibido dentro da MainFrame.
 */
public class CategoriasPanel extends JPanel {

    /** Tabela para exibir as categorias. */
    private JTable table;

    /** Modelo de dados da tabela. */
    private DefaultTableModel tableModel;

    /** Cliente para comunicação com a API. */
    private ApiClient apiClient;

    /** Cache local da lista de categorias vinda da API. */
    private List<CategoriaDTO> listaCategorias;

    /**
     * Constrói o painel de gerenciamento de categorias.
     *
     * @param apiClient A instância do cliente de API (passada pela MainFrame).
     */
    public CategoriasPanel(ApiClient apiClient) {
        this.apiClient = apiClient;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitulo = new JLabel("Gerenciar Categorias");
        lblTitulo.setFont(UIManager.getFont("h1.font"));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JButton btnAdicionar = new JButton("+ Adicionar Categoria");
        btnAdicionar.setFont(UIManager.getFont("h4.font"));
        btnAdicionar.setBackground(new Color(0x650f0f));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.addActionListener(e -> onAddCategoria());
        headerPanel.add(btnAdicionar, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] colunas = {"ID", "Nome", "Tamanho", "Embalagem"};
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
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setBackground(Color.WHITE);

        JButton btnEditar = new JButton("Editar Selecionada");
        btnEditar.addActionListener(e -> onEditCategoria());
        actionsPanel.add(btnEditar);

        JButton btnExcluir = new JButton("Excluir Selecionada");
        btnExcluir.addActionListener(e -> onDeleteCategoria());
        actionsPanel.add(btnExcluir);

        add(actionsPanel, BorderLayout.SOUTH);

        loadCategoriasData();
    }

    /**
     * Busca os dados de categorias da API em segundo plano (usando SwingWorker)
     * e atualiza a tabela na UI.
     */
    private void loadCategoriasData() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"Carregando...", "", "", ""});

        SwingWorker<List<CategoriaDTO>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<CategoriaDTO> doInBackground() throws Exception {
                // Chama a API para buscar as categorias
                return apiClient.getCategorias();
            }

            @Override
            protected void done() {
                tableModel.setRowCount(0);
                try {
                    listaCategorias = get(); // Pega o resultado da API
                    // Preenche a tabela com os dados
                    for (CategoriaDTO cat : listaCategorias) {
                        tableModel.addRow(new Object[]{
                                cat.getId(),
                                cat.getNome(),
                                cat.getTamanho(),
                                cat.getEmbalagem()
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CategoriasPanel.this,
                            "Erro ao carregar categorias: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    /**
     * Ação disparada pelo botão "Adicionar Categoria".
     * Abre o {@link CategoriaFormDialog} em modo de criação.
     */
    private void onAddCategoria() {
        // Abre o diálogo passando 'null' como categoria (modo de criação)
        CategoriaFormDialog dialog = new CategoriaFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                apiClient,
                null,
                this::loadCategoriasData // Callback para recarregar a tabela após salvar
        );
        dialog.setVisible(true);
    }

    /**
     * Ação disparada pelo botão "Editar Selecionada".
     * Abre o {@link CategoriaFormDialog} em modo de edição com a categoria selecionada.
     */
    private void onEditCategoria() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Pega a categoria do cache local baseado na linha selecionada
        CategoriaDTO selectedCategoria = listaCategorias.get(selectedRow);

        // Abre o diálogo passando a categoria selecionada
        CategoriaFormDialog dialog = new CategoriaFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                apiClient,
                selectedCategoria,
                this::loadCategoriasData // Callback para recarregar a tabela
        );
        dialog.setVisible(true);
    }

    /**
     * Ação disparada pelo botão "Excluir Selecionada".
     * Pede confirmação ao usuário e deleta a categoria selecionada via API.
     */
    private void onDeleteCategoria() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma categoria para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        CategoriaDTO selectedCategoria = listaCategorias.get(selectedRow);

        // Pede confirmação ao usuário
        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja excluir a categoria '" + selectedCategoria.getNome() + "'?\nIsso pode afetar produtos associados!",
                "Confirmar Exclusão", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Roda a exclusão em segundo plano
            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    apiClient.deleteCategoria(selectedCategoria.getId());
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get(); // Verifica se houve erro
                        JOptionPane.showMessageDialog(CategoriasPanel.this, "Categoria excluída com sucesso!");
                        loadCategoriasData(); // Recarrega a tabela
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(CategoriasPanel.this, "Erro ao excluir: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}