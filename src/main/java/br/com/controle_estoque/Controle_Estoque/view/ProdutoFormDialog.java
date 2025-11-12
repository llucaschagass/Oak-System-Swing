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

/**
 * Um JDialog modal para criar ou editar um Produto.
 * Lida com a coleta de dados do formulário e a comunicação com o ApiClient.
 */
public class ProdutoFormDialog extends JDialog {

    /** Cliente para comunicação com a API. */
    private ApiClient apiClient;

    /** Armazena o produto original no modo de edição (null se for criação). */
    private ProdutoDTO produtoExistente;

    /** Lista de categorias para preencher o JComboBox. */
    private List<CategoriaDTO> categorias;

    /** Função (callback) a ser executada após salvar, para atualizar a tabela principal. */
    private Runnable onSaveCallback;

    // Componentes do formulário
    private JTextField txtNome;
    private JTextField txtPreco;
    private JComboBox<String> cmbUnidade;
    private JSpinner spinQtdEstoque;
    private JSpinner spinQtdMinima;
    private JSpinner spinQtdMaxima;
    private JComboBox<CategoriaDTO> cmbCategoria;

    /**
     * Constrói o diálogo do formulário de produto.
     *
     * @param owner O Frame pai (a MainFrame).
     * @param apiClient A instância do cliente de API.
     * @param categorias A lista de categorias para o dropdown.
     * @param produto O produto a ser editado, ou null para criar um novo.
     * @param onSave A função (callback) a ser chamada após salvar.
     */
    public ProdutoFormDialog(Frame owner, ApiClient apiClient, List<CategoriaDTO> categorias, ProdutoDTO produto, Runnable onSave) {
        super(owner, true); // true = modal
        this.apiClient = apiClient;
        this.categorias = categorias;
        this.produtoExistente = produto;
        this.onSaveCallback = onSave;

        setTitle(isEditing() ? "Editar Produto" : "Adicionar Novo Produto");
        setSize(450, 400);
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
        gbc.weightx = 0; // Reset

        // Campo Preço
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Preço (R$):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        txtPreco = new JTextField();
        formPanel.add(txtPreco, gbc);

        // Campo Unidade
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Unidade:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2;
        String[] unidades = {"Unidade", "Pacote", "Caixa", "Kilograma", "Litro", "Metro"};
        cmbUnidade = new JComboBox<>(unidades);
        formPanel.add(cmbUnidade, gbc);

        // Campo Categoria
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Categoria:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3;
        cmbCategoria = new JComboBox<>(categorias.toArray(new CategoriaDTO[0]));
        cmbCategoria.setRenderer(new CategoriaComboBoxRenderer());
        formPanel.add(cmbCategoria, gbc);

        // Campo Qtd. Estoque
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Qtd. Estoque:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4;
        spinQtdEstoque = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(spinQtdEstoque, gbc);

        // Campo Qtd. Mínima
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Qtd. Mínima:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5;
        spinQtdMinima = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(spinQtdMinima, gbc);

        // Campo Qtd. Máxima
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Qtd. Máxima:"), gbc);
        gbc.gridx = 1; gbc.gridy = 6;
        spinQtdMaxima = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));
        formPanel.add(spinQtdMaxima, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnSalvar);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações ---
        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> handleSave());

        // Preenche o formulário se estiver no modo de edição
        if (isEditing()) {
            preencherFormulario();
        }
    }

    /**
     * Verifica se o formulário está em modo de edição.
     * @return true se {@code produtoExistente} não é nulo.
     */
    private boolean isEditing() {
        return produtoExistente != null;
    }

    /**
     * Preenche os campos do formulário com os dados do produto existente.
     * Chamado apenas no modo de edição.
     */
    private void preencherFormulario() {
        txtNome.setText(produtoExistente.getNome());
        txtPreco.setText(produtoExistente.getPrecoUnitario().toString());
        cmbUnidade.setSelectedItem(produtoExistente.getUnidade());
        spinQtdEstoque.setValue(produtoExistente.getQuantidadeEmEstoque());
        spinQtdMinima.setValue(produtoExistente.getQuantidadeMinima());
        spinQtdMaxima.setValue(produtoExistente.getQuantidadeMaxima());

        // Encontra e seleciona a categoria correta no JComboBox
        for (int i = 0; i < categorias.size(); i++) {
            if (categorias.get(i).getId() == produtoExistente.getCategoria().getId()) {
                cmbCategoria.setSelectedIndex(i);
                break;
            }
        }
    }

    /**
     * Ação do botão "Salvar".
     * Valida os dados, cria o DTO de payload e chama a API
     * (create ou update) em um {@link SwingWorker} para não travar a UI.
     */
    private void handleSave() {
        try {
            // 1. Coletar dados da UI
            String nome = txtNome.getText();
            // Substitui vírgula por ponto para o BigDecimal
            BigDecimal preco = new BigDecimal(txtPreco.getText().replace(",", "."));
            String unidade = (String) cmbUnidade.getSelectedItem();
            int qtdEstoque = (int) spinQtdEstoque.getValue();
            int qtdMinima = (int) spinQtdMinima.getValue();
            int qtdMaxima = (int) spinQtdMaxima.getValue();
            CategoriaDTO categoriaSelecionada = (CategoriaDTO) cmbCategoria.getSelectedItem();

            // 2. Validar
            if (nome.isEmpty() || categoriaSelecionada == null) {
                JOptionPane.showMessageDialog(this, "Nome e Categoria são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 3. Montar Payload
            ProdutoPayloadDTO payload = new ProdutoPayloadDTO(
                    nome, preco, unidade, qtdEstoque, qtdMinima, qtdMaxima, categoriaSelecionada.getId()
            );

            // 4. Executar em background
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
                        get(); // Verifica se houve exceções
                        JOptionPane.showMessageDialog(ProdutoFormDialog.this,
                                "Produto salvo com sucesso!",
                                "Sucesso",
                                JOptionPane.INFORMATION_MESSAGE);
                        onSaveCallback.run(); // Atualiza a tabela principal
                        dispose(); // Fecha o pop-up
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
            JOptionPane.showMessageDialog(this, "Preço inválido. Use o formato 123.45 ou 123,45", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }
}