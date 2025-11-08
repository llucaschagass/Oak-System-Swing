package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.auth.AuthManager;
import br.com.controle_estoque.Controle_Estoque.client.ApiClient;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private ApiClient apiClient;

    public MainFrame() {
        this.apiClient = new ApiClient();

        setTitle("Controle de Estoque");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(0x650f0f));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));

        sidebarPanel.add(createMenuButton("Dashboard"));
        sidebarPanel.add(createMenuButton("Produtos"));
        sidebarPanel.add(createMenuButton("Categorias"));
        sidebarPanel.add(createMenuButton("Movimentações"));
        sidebarPanel.add(createMenuButton("Relatórios"));

        sidebarPanel.add(Box.createVerticalGlue());

        JButton btnSair = new JButton("Sair");
        configureMenuButton(btnSair);
        btnSair.addActionListener(e -> handleLogout());
        sidebarPanel.add(btnSair);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        contentPanel.add(new DashboardPanel(this.apiClient), "DASHBOARD");
        contentPanel.add(new ProdutosPanel(this.apiClient), "PRODUTOS");
        contentPanel.add(new CategoriasPanel(this.apiClient), "CATEGORIAS");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        cardLayout.show(contentPanel, "DASHBOARD");
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        configureMenuButton(button);

        button.addActionListener(e -> {
            String cardName = text.toUpperCase();

            if (cardName.equals("DASHBOARD") || cardName.equals("PRODUTOS") || cardName.equals("CATEGORIAS")) {
                cardLayout.show(contentPanel, cardName);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Tela '" + text + "' ainda não implementada.",
                        "Em Construção",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        return button;
    }

    private void configureMenuButton(JButton button) {
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0x650f0f));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
    }

    private void handleLogout() {
        AuthManager.logout();
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}