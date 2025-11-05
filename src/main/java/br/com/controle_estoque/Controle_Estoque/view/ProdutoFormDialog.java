package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.CategoriaDTO;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutoDTO;
import br.com.controle_estoque.Controle_Estoque.dto.ProdutoPayloadDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.math.BigDecimal;
import java.util.List;

public class ProdutoFormDialog extends JDialog {

    private ApiClient apiClient;
    private ProdutoDTO produtoExistente;
    private List<CategoriaDTO> categorias;
    private Runnable onSaveCallback;
    private JTextField txtNome;
    private JTextField txtPreco;
    private JComboBox<String> cmbUnidade;
    private JSpinner spinQtdEstoque;
    private JSpinner spinQtdMinima;
    private JSpinner spinQtdMaxima;
    private JComboBox<CategoriaDTO> cmbCategoria;

    public ProdutoFormDialog(Frame owner, ApiClient apiClient, List<CategoriaDTO> categorias, ProdutoDTO produto, Runnable onSave) {
        super(owner, true);
        this.apiClient = apiClient;
        this.categorias = categorias;
        this.produtoExistente = produto;
        this.onSaveCallback = onSave;
        this.produtoExistente = produto;

        setTitle(isEditing() ? "Editar Produto" : "Adicionar Novo Produto");
        setSize(450, 400);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        formPanel.add(txtNome, gbc);
        gbc.weightx = 0;

        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtPreco = new JTextField();
        formPanel.add(txtPreco, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Unidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        String[] unidades = {"Unidade", "Pacote", "Caixa", "Kilograma", "Litro", "Metro"};
        cmbUnidade = new JComboBox<>(unidades);
        formPanel.add(cmbUnidade, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        cmbCategoria = new JComboBox<>(categorias.toArray(new CategoriaDTO[0]));
        cmbCategoria.setRenderer(new CategoriaComboBoxRenderer());
        formPanel.add(cmbCategoria, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Qtd. Estoque:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        spinQtdEstoque = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(spinQtdEstoque, gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Qtd. Mínima:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        spinQtdMinima = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(spinQtdMinima, gbc);

        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Qtd. Máxima:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        spinQtdMaxima = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(spinQtdMaxima, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        add(buttonPanel, BorderLayout.SOUTH);

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> handleSave());

        if (isEditing()) {
            preencherFormulario();
        }
    }

    private boolean isEditing() {
        return produtoExistente != null;
    }

    private void preencherFormulario() {
        txtNome.setText(produtoExistente.getNome());
        txtPreco.setText(produtoExistente.getPrecoUnitario().toString());
        cmbUnidade.setSelectedItem(produtoExistente.getUnidade());
        spinQtdEstoque.setValue(produtoExistente.getQuantidadeEmEstoque());
        spinQtdMinima.setValue(produtoExistente.getQuantidadeMinima());
        spinQtdMaxima.setValue(produtoExistente.getQuantidadeMaxima());

        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == produtoExistente.getCategoria().getId()) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    private void handleSave() {
        try {
            String nome = txtNome.getText();
            BigDecimal preco = new BigDecimal(txtPreco.getText());
            String unidade = (String) cmbUnidade.getSelectedItem();
            int qtdEstoque = (int) spinQtdEstoque.getValue();
            int qtdMinima = (int) spinQtdMinima.getValue();
            int qtdMaxima = (int) spinQtdMaxima.getValue();
            CategoriaDTO categoriaSelecionada = (CategoriaDTO) cmbCategoria.getSelectedItem();

            if (nome.isEmpty() || categoriaSelecionada == null) {
                JOptionPane.showMessageDialog(this, "Nome e Categoria são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            ProdutoPayloadDTO payload = new ProdutoPayloadDTO(
                    nome, preco, unidade, qtdEstoque, qtdMinima, qtdMaxima, categoriaSelecionada.getId()
            );

            SwingWorker<Void, Void> worker = new SwingWorker<>() {
                @Override
                protected Void doInBackground() throws Exception {
                    if (isEditing()) {
                        apiClient.updateProduto(produtoExistente.getId(), payload);
                    } else {
                        apiClient.createProduto(payload);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        JOptionPane.showMessageDialog(ProdutoFormDialog.this,
                                "Produto salvo com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                        onSaveCallback.run();
                        dispose();
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(ProdutoFormDialog.this,
                                "Erro ao salvar produto: " + e.getMessage(),
                                "Erro",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço inválido. Use o formato 123.45", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}