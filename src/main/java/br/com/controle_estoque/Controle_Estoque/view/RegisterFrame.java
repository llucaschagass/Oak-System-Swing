package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.auth.AuthManager;
import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.AuthenticationResponseDTO;
import br.com.controle_estoque.Controle_Estoque.dto.RegisterRequestDTO;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/**
 * JDialog (pop-up) modal para o formulário de registro de novo usuário.
 */
public class RegisterFrame extends JDialog {

    private JTextField txtNome;
    private JTextField txtUsuario;
    private JTextField txtEmail;
    private JTextField txtTelefone;
    private JPasswordField txtSenha;
    private JPasswordField txtConfirmaSenha;
    private JButton btnRegistrar;
    private ApiClient apiClient;

    /**
     * Constrói o diálogo de registro.
     *
     * @param owner O Frame pai (a LoginFrame).
     * @param apiClient A instância do cliente de API.
     */
    public RegisterFrame(Frame owner, ApiClient apiClient) {
        super(owner, "Registrar Novo Usuário", true);
        this.apiClient = apiClient;

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

        txtNome = new JTextField(20);
        txtUsuario = new JTextField(20);
        txtEmail = new JTextField(20);
        txtTelefone = new JTextField(20);
        txtSenha = new JPasswordField(20);
        txtConfirmaSenha = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0; formPanel.add(new JLabel("Nome Completo:"), gbc);
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 1.0; formPanel.add(txtNome, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; formPanel.add(new JLabel("Usuário (login):"), gbc);
        gbc.gridx = 1; gbc.gridy = 1; formPanel.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 2; formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1; gbc.gridy = 2; formPanel.add(txtEmail, gbc);

        gbc.gridx = 0; gbc.gridy = 3; formPanel.add(new JLabel("Telefone:"), gbc);
        gbc.gridx = 1; gbc.gridy = 3; formPanel.add(txtTelefone, gbc);

        gbc.gridx = 0; gbc.gridy = 4; formPanel.add(new JLabel("Senha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 4; formPanel.add(txtSenha, gbc);

        gbc.gridx = 0; gbc.gridy = 5; formPanel.add(new JLabel("Confirmar Senha:"), gbc);
        gbc.gridx = 1; gbc.gridy = 5; formPanel.add(txtConfirmaSenha, gbc);

        add(formPanel, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnRegistrar = new JButton("Registrar");
        JButton btnCancelar = new JButton("Cancelar");

        btnRegistrar.setBackground(new Color(0x650f0f));
        btnRegistrar.setForeground(Color.WHITE);

        buttonPanel.add(btnCancelar);
        buttonPanel.add(btnRegistrar);
        add(buttonPanel, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnRegistrar);

        btnCancelar.addActionListener(e -> dispose());
        btnRegistrar.addActionListener(e -> handleRegister());
    }

    /**
     * Valida o formulário e tenta registrar o usuário via API.
     */
    private void handleRegister() {
        String nome = txtNome.getText().trim();
        String usuario = txtUsuario.getText().trim();
        String email = txtEmail.getText().trim();
        String telefone = txtTelefone.getText().trim();
        char[] senhaChars = txtSenha.getPassword();
        char[] confirmaSenhaChars = txtConfirmaSenha.getPassword();

        if (nome.isEmpty() || usuario.isEmpty() || email.isEmpty() || senhaChars.length == 0) {
            JOptionPane.showMessageDialog(this, "Nome, Usuário, Email e Senha são obrigatórios.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!Arrays.equals(senhaChars, confirmaSenhaChars)) {
            JOptionPane.showMessageDialog(this, "As senhas não coincidem.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String senha = new String(senhaChars);

        btnRegistrar.setEnabled(false);
        btnRegistrar.setText("Registrando...");

        RegisterRequestDTO registerRequest = new RegisterRequestDTO(nome, usuario, email, telefone, senha);

        SwingWorker<AuthenticationResponseDTO, Void> worker = new SwingWorker<>() {
            @Override
            protected AuthenticationResponseDTO doInBackground() throws Exception {
                return apiClient.register(registerRequest);
            }

            @Override
            protected void done() {
                try {
                    AuthenticationResponseDTO response = get();
                    AuthManager.setToken(response.getToken());

                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Usuário registrado com sucesso!\nLogin automático realizado.",
                            "Sucesso",
                            JOptionPane.INFORMATION_MESSAGE);

                    dispose(); // Fecha o pop-up de registro

                    ((Window) getOwner()).dispose();

                    // Abre a tela principal
                    new MainFrame().setVisible(true);

                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(RegisterFrame.this,
                            "Erro ao registrar: " + e.getMessage(),
                            "Erro", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnRegistrar.setEnabled(true);
                    btnRegistrar.setText("Registrar");
                }
            }
        };
        worker.execute();
    }
}