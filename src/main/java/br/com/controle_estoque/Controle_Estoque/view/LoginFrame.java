package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.auth.AuthManager;
import br.com.controle_estoque.Controle_Estoque.client.ApiClient;
import br.com.controle_estoque.Controle_Estoque.dto.AuthenticationResponseDTO;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * JFrame (janela) inicial da aplicação, responsável pela autenticação do usuário.
 * Contém os campos para login e um botão para acionar a tela de registro.
 */
public class LoginFrame extends JFrame {

    // --- Componentes da UI ---
    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnRegistrar;

    /** Cliente para comunicação com a API. */
    private ApiClient apiClient;

    /**
     * Constrói a janela de login.
     * Inicializa o ApiClient, configura a interface gráfica (Swing) e
     * associa os ActionListeners aos botões.
     */
    public LoginFrame() {
        this.apiClient = new ApiClient();

        // Configurações da janela
        setTitle("Controle de Estoque - Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza na tela
        setResizable(false);

        // --- Montagem do Painel com GridBagLayout ---
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8); // Espaçamento

        gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Usuário:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUsuario = new JTextField(20);
        mainPanel.add(txtUsuario, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        mainPanel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        txtSenha = new JPasswordField(20);
        mainPanel.add(txtSenha, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnEntrar = new JButton("Entrar");
        btnRegistrar = new JButton("Registrar");

        getRootPane().setDefaultButton(btnEntrar); // Permite "Enter" para logar
        buttonPanel.add(btnEntrar);
        buttonPanel.add(btnRegistrar);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        // --- Definição das Ações ---

        /**
         * Ação do botão "Entrar". Chama o método handleLogin.
         */
        btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });

        /**
         * Ação do botão "Registrar". Abre o JDialog RegisterFrame.
         */
        btnRegistrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Passa esta janela (this) como 'owner' do pop-up
                RegisterFrame registerFrame = new RegisterFrame(LoginFrame.this, apiClient);
                registerFrame.setVisible(true);
            }
        });
    }

    /**
     * Lida com o evento de clique do botão "Entrar".
     * Valida os campos e inicia o {@link SwingWorker} para autenticação na API.
     */
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String senha = new String(txtSenha.getPassword());

        // Validação de campos vazios
        if (usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Usuário e senha não podem estar vazios.",
                    "Erro de Login",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Desabilita o botão para feedback visual
        btnEntrar.setEnabled(false);
        btnEntrar.setText("Entrando...");

        /**
         * Usa um SwingWorker para executar a chamada de API em uma thread separada,
         * impedindo que a interface gráfica (UI) congele.
         */
        SwingWorker<AuthenticationResponseDTO, Void> worker = new SwingWorker<>() {

            /**
             * Ação executada em background (outra thread).
             * Chama a API para tentar o login.
             *
             * @return O DTO de resposta da autenticação contendo o token.
             * @throws Exception Se o login falhar.
             */
            @Override
            protected AuthenticationResponseDTO doInBackground() throws Exception {
                return apiClient.login(usuario, senha);
            }

            /**
             * Ação executada na thread da UI após o 'doInBackground' terminar.
             * Processa o resultado do login (sucesso ou falha).
             */
            @Override
            protected void done() {
                try {
                    // Pega o resultado da chamada da API
                    AuthenticationResponseDTO response = get();

                    // Salva o token no gerenciador de autenticação
                    AuthManager.setToken(response.getToken());

                    // Fecha a janela de login
                    dispose();

                    // Abre a janela principal da aplicação
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);

                } catch (Exception e) {
                    // Se 'get()' lançar uma exceção (ex: falha no login 403)
                    e.printStackTrace();
                    AuthManager.logout(); // Garante que não há token salvo
                    JOptionPane.showMessageDialog(LoginFrame.this,
                            "Falha no login. Verifique usuário e senha.",
                            "Erro de Login",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Reabilita o botão, independente do resultado
                    btnEntrar.setEnabled(true);
                    btnEntrar.setText("Entrar");
                }
            }
        };

        worker.execute(); // Inicia o SwingWorker
    }
}