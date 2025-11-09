package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutoDTO;
import br.com.controle_estoque.Controle_Estoque.dto.MovimentacaoPayloadDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MovimentacaoFormDialog extends JDialog {

    private ApiClient apiClient;
    private List<ProdutoDTO> produtos;
    private Runnable onSaveCallback;
    private JComboBox<ProdutoDTO> cmbProduto;
    private JSpinner spinQuantidade;
    private JComboBox<String> cmbTipo;

    public MovimentacaoFormDialog(Frame owner, ApiClient apiClient, List<ProdutoDTO> produtos, Runnable onSave) {
        super(owner, true);
        this.apiClient = apiClient;
        this.produtos = produtos;
        this.onSaveCallback = onSave;

        setTitle("Registrar Nova Movimentação");
        setSize(400, 250);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Produto:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        cmbProduto = new JComboBox<>(produtos.toArray(new ProdutoDTO[0]));
        cmbProduto.setSelectedIndex(-1);
        formPanel.add(cmbProduto, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Quantidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        spinQuantidade = new JSpinner(new SpinnerNumberModel(1, 1, 9999, 1));
        formPanel.add(spinQuantidade, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Tipo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        cmbTipo = new JComboBox<>(new String[]{"ENTRADA", "SAIDA"});
        formPanel.add(cmbTipo, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.setBackground(new Color(0x650f0f));
        btnSalvar.setForeground(Color.WHITE);

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        add(buttonPanel, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> handleSave());
    }

    private void handleSave() {
        ProdutoDTO produtoSelecionado = (ProdutoDTO) cmbProduto.getSelectedItem();
        int quantidade = (int) spinQuantidade.getValue();
        String tipo = (String) cmbTipo.getSelectedItem();

        if (produtoSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione um produto.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        MovimentacaoPayloadDTO payload = new MovimentacaoPayloadDTO(
                produtoSelecionado.getId(),
                quantidade,
                tipo
        );

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                apiClient.createMovimentacao(payload);
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(MovimentacaoFormDialog.this, "Movimentação registrada com sucesso!");
                    onSaveCallback.run();
                    dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(MovimentacaoFormDialog.this,
                            "Erro ao registrar: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}