package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.CategoriaDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Um JDialog modal para criar ou editar uma Categoria.
 */
public class CategoriaFormDialog extends JDialog {

    /** Cliente de API para fazer as chamadas HTTP. */
    private ApiClient apiClient;

    /** Armazena a categoria original no modo de edição. */
    private CategoriaDTO categoriaExistente;

    /** Função (callback) a ser executada após salvar, para atualizar a tabela principal. */
    private Runnable onSaveCallback;

    // Componentes do formulário
    private JTextField txtNome;
    private JComboBox<String> cmbTamanho;
    private JComboBox<String> cmbEmbalagem;

    /**
     * Constrói o diálogo do formulário de categoria.
     *
     * @param owner O Frame pai (a MainFrame).
     * @param apiClient A instância do cliente de API.
     * @param categoria A categoria a ser editada, ou null para criar uma nova.
     * @param onSave A função a ser chamada após salvar com sucesso.
     */
    public CategoriaFormDialog(Frame owner, ApiClient apiClient, CategoriaDTO categoria, Runnable onSave) {
        super(owner, true); // true = modal (bloqueia a janela pai)
        this.apiClient = apiClient;
        this.categoriaExistente = categoria;
        this.onSaveCallback = onSave;

        setTitle(isEditing() ? "Editar Categoria" : "Adicionar Nova Categoria");
        setSize(400, 300);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout(10, 10));

        // --- Painel do Formulário ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Campo Nome
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nome:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0;
        txtNome = new JTextField(20);
        formPanel.add(txtNome, gbc);

        // Campo Tamanho
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0;
        formPanel.add(new JLabel("Tamanho:"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        String[] tamanhos = {"Pequeno", "Médio", "Grande"};
        cmbTamanho = new JComboBox<>(tamanhos);
        formPanel.add(cmbTamanho, gbc);

        // Campo Embalagem
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Embalagem:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        String[] embalagens = {"Lata", "Vidro", "Plástico"};
        cmbEmbalagem = new JComboBox<>(embalagens);
        formPanel.add(cmbEmbalagem, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.setBackground(new Color(0x650f0f)); // Cor primária
        btnSalvar.setForeground(Color.WHITE);

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações ---
        btnCancelar.addActionListener(e -> dispose()); // Fecha o diálogo
        btnSalvar.addActionListener(e -> handleSave());

        // Se for modo de edição, preenche os campos com os dados existentes
        if (isEditing()) {
            preencherFormulario();
        }
    }

    /**
     * Verifica se o formulário está em modo de edição.
     * @return true se estiver editando, false se estiver adicionando.
     */
    private boolean isEditing() {
        return categoriaExistente != null;
    }

    /**
     * Preenche os campos do formulário com os dados da categoria existente.
     */
    private void preencherFormulario() {
        txtNome.setText(categoriaExistente.getNome());
        cmbTamanho.setSelectedItem(categoriaExistente.getTamanho());
        cmbEmbalagem.setSelectedItem(categoriaExistente.getEmbalagem());
    }

    /**
     * Valida os campos e envia os dados para a API (criar ou atualizar).
     * Roda a chamada de API em um SwingWorker para não travar a UI.
     */
    private void handleSave() {
        String nome = txtNome.getText().trim();
        if (nome.isEmpty()) {
            JOptionPane.showMessageDialog(this, "O nome da categoria é obrigatório.", "Erro", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Prepara o DTO para envio
        CategoriaDTO novaCategoria = new CategoriaDTO();
        novaCategoria.setNome(nome);
        novaCategoria.setTamanho((String) cmbTamanho.getSelectedItem());
        novaCategoria.setEmbalagem((String) cmbEmbalagem.getSelectedItem());

        // Roda a chamada de API em segundo plano
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (isEditing()) {
                    // Se estiver editando, chama o método de atualização
                    apiClient.updateCategoria(categoriaExistente.getId(), novaCategoria);
                } else {
                    // Se estiver criando, chama o método de criação
                    apiClient.createCategoria(novaCategoria);
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get(); // Pega o resultado (e verifica se houve exceção)
                    JOptionPane.showMessageDialog(CategoriaFormDialog.this, "Categoria salva com sucesso!");
                    onSaveCallback.run(); // Atualiza a tabela na tela principal
                    dispose(); // Fecha este diálogo
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