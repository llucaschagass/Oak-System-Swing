package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.CategoriaDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CategoriaFormDialog extends JDialog {

    private ApiClient apiClient;
    private CategoriaDTO categoriaExistente;
    private Runnable onSaveCallback;

    private JTextField txtNome;
    private JComboBox<String> cmbTamanho;
    private JComboBox<String> cmbEmbalagem;

    public CategoriaFormDialog(Frame owner, ApiClient apiClient, CategoriaDTO categoria, Runnable onSave) {
        super(owner, true);
        this.apiClient = apiClient;
        this.categoriaExistente = categoria;
        this.onSaveCallback = onSave;

        setTitle(isEditing() ? "Editar Categoria" : "Adicionar Nova Categoria");
        setSize(400, 300);
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

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Tamanho:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        String[] tamanhos = {"Pequeno", "Médio", "Grande"};
        cmbTamanho = new JComboBox<>(tamanhos);
        formPanel.add(cmbTamanho, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Embalagem:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        String[] embalagens = {"Lata", "Vidro", "Plástico"};
        cmbEmbalagem = new JComboBox<>(embalagens);
        formPanel.add(cmbEmbalagem, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.setBackground(new Color(0x650f0f));
        btnSalvar.setForeground(Color.WHITE);

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
        return categoriaExistente != null;
    }

    private void preencherFormulario() {
        txtNome.setText(categoriaExistente.getNome());
        cmbTamanho.setSelectedItem(categoriaExistente.getTamanho());
        cmbEmbalagem.setSelectedItem(categoriaExistente.getEmbalagem());
    }

    private void handleSave() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da categoria é obrigatório.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        CategoriaDTO novaCategoria = new CategoriaDTO();
        novaCategoria.setNome(nome);
        novaCategoria.setTamanho((String) cmbTamanho.getSelectedItem());
        novaCategoria.setEmbalagem((String) cmbEmbalagem.getSelectedItem());

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (isEditing()) {
                    apiClient.updateCategoria(categoriaExistente.getId(), novaCategoria);
                } else {
                    apiClient.createCategoria(novaCategoria);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    JOptionPane.showMessageDialog(CategoriaFormDialog.this, "Categoria salva com sucesso!");
                    onSaveCallback.run();
                    dispose();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(CategoriaFormDialog.this,
                            "Erro ao salvar categoria: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}