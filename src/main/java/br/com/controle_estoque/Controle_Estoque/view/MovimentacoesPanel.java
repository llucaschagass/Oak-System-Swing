package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.MovimentacaoDTO;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MovimentacoesPanel extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private ApiClient apiClient;

    private List<MovimentacaoDTO> listaMovimentacoes;
    private List<ProdutoDTO> listaProdutos;

    public MovimentacoesPanel(ApiClient apiClient) {
        this.apiClient = apiClient;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitulo = new JLabel("Movimentações de Estoque");
        lblTitulo.setFont(UIManager.getFont("h1.font"));
        headerPanel.add(lblTitulo, BorderLayout.WEST);

        JButton btnAdicionar = new JButton("+ Registrar Movimentação");
        btnAdicionar.setFont(UIManager.getFont("h4.font"));
        btnAdicionar.setBackground(new Color(0x650f0f));
        btnAdicionar.setForeground(Color.WHITE);
        btnAdicionar.setFocusPainted(false);
        btnAdicionar.addActionListener(e -> onAddMovimentacao());
        headerPanel.add(btnAdicionar, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        String[] colunas = {"Data/Hora", "Produto", "Tipo", "Quantidade"};
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

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(new TipoMovimentacaoRenderer());

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);

        loadInitialData();
    }

    private void loadInitialData() {
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{"Carregando...", "", "", ""});

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private List<MovimentacaoDTO> movs;
            private List<ProdutoDTO> prods;
            private String error = null;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    movs = apiClient.getMovimentacoes();
                    prods = apiClient.getProdutos();
                } catch (Exception e) {
                    error = e.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                tableModel.setRowCount(0);
                if (error != null) {
                    JOptionPane.showMessageDialog(MovimentacoesPanel.this,
                            "Erro ao carregar dados: " + error, "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    listaMovimentacoes = movs;
                    listaProdutos = prods;
                    preencherTabela();
                }
            }
        };
        worker.execute();
    }

    private void preencherTabela() {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        for (MovimentacaoDTO mov : listaMovimentacoes) {
            tableModel.addRow(new Object[]{
                    mov.getDataMovimentacao().format(formatter),
                    mov.getProduto().getNome(),
                    mov.getTipoMovimentacao(),
                    mov.getQuantidadeMovimentada()
            });
        }
    }

    private void onAddMovimentacao() {
        if (listaProdutos == null || listaProdutos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Lista de produtos ainda carregando. Tente novamente.", "Aguarde", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MovimentacaoFormDialog dialog = new MovimentacaoFormDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                apiClient,
                listaProdutos,
                this::loadInitialData
        );
        dialog.setVisible(true);
    }

    static class TipoMovimentacaoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String tipo = (String) value;

            if ("ENTRADA".equals(tipo)) {
                c.setForeground(new Color(0, 128, 0));
            } else if ("SAIDA".equals(tipo)) {
                c.setForeground(Color.RED);
            } else {
                c.setForeground(table.getForeground());
            }
            return c;
        }
    }
}