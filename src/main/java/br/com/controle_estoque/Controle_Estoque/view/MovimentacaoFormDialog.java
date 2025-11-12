package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutoDTO;
import br.com.controle_estoque.Controle_Estoque.dto.MovimentacaoPayloadDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Um JDialog modal para registrar uma nova Movimentação (Entrada ou Saída).
 */
public class MovimentacaoFormDialog extends JDialog {

    /** Cliente para comunicação com a API. */
    private ApiClient apiClient;

    /** Lista de produtos disponíveis para selecionar. */
    private List<ProdutoDTO> produtos;

    /** Função (callback) a ser executada após salvar, para atualizar a tabela principal. */
    private Runnable onSaveCallback;

    // --- Componentes do Formulário ---
    /** Dropdown para selecionar o produto. */
    private JComboBox<ProdutoDTO> cmbProduto;

    /** Campo numérico para a quantidade. */
    private JSpinner spinQuantidade;

    /** Dropdown para selecionar o tipo (ENTRADA ou SAIDA). */
    private JComboBox<String> cmbTipo;

    /**
     * Constrói o diálogo do formulário de movimentação.
     *
     * @param owner O Frame pai (a MainFrame).
     * @param apiClient A instância do cliente de API.
     * @param produtos A lista de produtos para preencher o dropdown.
     * @param onSave A função a ser chamada após salvar com sucesso.
     */
    public MovimentacaoFormDialog(Frame owner, ApiClient apiClient, List<ProdutoDTO> produtos, Runnable onSave) {
        super(owner, true); // true = modal
        this.apiClient = apiClient;
        this.produtos = produtos;
        this.onSaveCallback = onSave;

        setTitle("Registrar Nova Movimentação");
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- Painel do Formulário ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo Produto
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        cmbProduto = new JComboBox<>(produtos.toArray(new ProdutoDTO[0]));
        cmbProduto.setSelectedIndex(-1); // Inicia sem seleção
        formPanel.add(cmbProduto, gbc);

        // Campo Quantidade
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        spinQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1)); // Mínimo 1
        formPanel.add(spinQuantidade, gbc);

        // Campo Tipo
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        cmbTipo = new JComboBox<>(new String[]{"ENTRADA", "SAIDA"});
        formPanel.add(cmbTipo, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.setBackground(new Color(0x650f0f));
        btnSalvar.setForeground(Color.WHITE);

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações ---
        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> handleSave());
    }

    /**
     * Valida os campos e envia a nova movimentação para a API.
     * Roda a chamada de API em um SwingWorker para não travar a UI.
     */
    private void handleSave() {
        ProdutoDTO produtoSelecionado = (ProdutoDTO) cmbProduto.getSelectedItem();
        int quantidade = (int) spinQuantidade.getValue();
        String tipo = (String) cmbTipo.getSelectedItem();

        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Monta o DTO de payload para a API
        MovimentacaoPayloadDTO payload = new MovimentacaoPayloadDTO(
                produtoSelecionado.getId(),
                quantidade,
                tipo
        );

        // Roda a chamada de API em segundo plano
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Chama o método de criação do ApiClient
                apiClient.createMovimentacao(payload);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Verifica se houve exceção (ex: estoque insuficiente)
                    JOptionPane.showMessageDialog(MovimentacaoFormDialog.this, "Movimentação registrada com sucesso!");
                    onSaveCallback.run(); // Atualiza a tabela principal
                    dispose(); // Fecha este diálogo
                } catch (Exception e) {
                    e.printStackTrace();
                    // Mostra a mensagem de erro vinda da API
                    JOptionPane.showMessageDialog(MovimentacaoFormDialog.this,
                            "Erro ao registrar: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}