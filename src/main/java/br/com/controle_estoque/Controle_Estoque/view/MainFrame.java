package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.auth.AuthManager;
import br.com.controle_estoque.Controle_Estoque.client.ApiClient;

import javax.swing.*;
import java.awt.*;

/**
 * Representa a janela principal da aplicação após o login.
 * Este JFrame contém o menu lateral (sidebar) e a área de conteúdo principal
 * que utiliza um {@link CardLayout} para alternar entre os diferentes painéis
 * (Dashboard, Produtos, etc.).
 */
public class MainFrame extends JFrame {

    /** Painel do menu lateral. */
    private JPanel sidebarPanel;

    /** Painel que armazena os diferentes painéis de conteúdo (telas). */
    private JPanel contentPanel;

    /** Gerenciador de layout que permite alternar entre os painéis de conteúdo. */
    private CardLayout cardLayout;

    /** Instância única do cliente de API, passada para os painéis filhos. */
    private ApiClient apiClient;

    /**
     * Constrói a janela principal.
     * Inicializa o ApiClient, configura o layout, cria o menu lateral
     * e adiciona todos os painéis de gerenciamento ao CardLayout.
     */
    public MainFrame() {
        this.apiClient = new ApiClient();

        setTitle("Controle de Estoque");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Criação do Menu Lateral ---
        sidebarPanel = new JPanel();
        sidebarPanel.setBackground(new Color(0x650f0f));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setPreferredSize(new Dimension(250, 0));

        sidebarPanel.add(createMenuButton("Dashboard"));
        sidebarPanel.add(createMenuButton("Produtos"));
        sidebarPanel.add(createMenuButton("Categorias"));
        sidebarPanel.add(createMenuButton("Movimentações"));
        sidebarPanel.add(createMenuButton("Relatórios"));

        sidebarPanel.add(Box.createVerticalGlue()); // Empurra o botão Sair para baixo

        JButton btnSair = new JButton("Sair");
        configureMenuButton(btnSair);
        btnSair.addActionListener(e -> handleLogout());
        sidebarPanel.add(btnSair);

        // --- Criação do Painel de Conteúdo ---
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);

        // Adiciona todos os painéis ao CardLayout com suas chaves (nomes)
        contentPanel.add(new DashboardPanel(this.apiClient), "DASHBOARD");
        contentPanel.add(new ProdutosPanel(this.apiClient), "PRODUTOS");
        contentPanel.add(new CategoriasPanel(this.apiClient), "CATEGORIAS");
        contentPanel.add(new MovimentacoesPanel(this.apiClient), "MOVIMENTAÇÕES");
        contentPanel.add(new RelatoriosPanel(this.apiClient), "RELATÓRIOS");

        add(sidebarPanel, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        // Define o Dashboard como a tela inicial
        cardLayout.show(contentPanel, "DASHBOARD");
    }

    /**
     * Método fábrica para criar e configurar um botão do menu lateral.
     * Associa uma ação ao botão para trocar o painel no CardLayout.
     *
     * @param text O texto a ser exibido no botão.
     * @return Um {@link JButton} estilizado e com ação.
     */
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        configureMenuButton(button);

        button.addActionListener(e -> {
            // Converte o texto do botão (ex: "Movimentações") para a chave (ex: "MOVIMENTAÇÕES")
            String cardName = text.toUpperCase();

            // Lista de painéis que estão prontos para serem exibidos
            // (Esta verificação é redundante agora que todos estão implementados, mas é uma boa prática)
            if (cardName.equals("DASHBOARD") ||
                    cardName.equals("PRODUTOS") ||
                    cardName.equals("CATEGORIAS") ||
                    cardName.equals("MOVIMENTAÇÕES") ||
                    cardName.equals("RELATÓRIOS")){

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

    /**
     * Aplica o estilo visual padrão aos botões do menu lateral.
     *
     * @param button O {@link JButton} a ser estilizado.
     */
    private void configureMenuButton(JButton button) {
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0x650f0f));
        button.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        // Garante que o botão se expanda horizontalmente
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, button.getPreferredSize().height));
    }

    /**
     * Lida com a ação de logout.
     * Limpa o token do {@link AuthManager}, fecha a janela principal
     * e abre uma nova janela de login.
     */
    private void handleLogout() {
        AuthManager.logout();
        this.dispose(); // Fecha a MainFrame
        new LoginFrame().setVisible(true); // Abre a LoginFrame
    }
}