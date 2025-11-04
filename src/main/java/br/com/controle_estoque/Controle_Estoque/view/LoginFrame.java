package br.com.controle_estoque.Controle_Estoque.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginFrame extends JFrame {

    private JTextField txtUsuario;
    private JPasswordField txtSenha;
    private JButton btnEntrar;
    private JButton btnRegistrar;

    public LoginFrame() {
        setTitle("Controle de Estoque - Login");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        mainPanel.add(new JLabel("Usuário:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUsuario = new JTextField(20);
        mainPanel.add(txtUsuario, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Senha:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        txtSenha = new JPasswordField(20);
        mainPanel.add(txtSenha, gbc);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        btnEntrar = new JButton("Entrar");
        btnRegistrar = new JButton("Registrar");

        getRootPane().setDefaultButton(btnEntrar);

        buttonPanel.add(btnEntrar);
        buttonPanel.add(btnRegistrar);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        add(mainPanel);

        btnEntrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String senha = new String(txtSenha.getPassword());

        if (usuario.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Usuário e senha não podem estar vazios.",
                    "Erro de Login",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        System.out.println("Tentando logar com Usuário: " + usuario);
        JOptionPane.showMessageDialog(this,
                "Lógica de login com API ainda não implementada!",
                "Aguarde",
                JOptionPane.INFORMATION_MESSAGE);
    }
}