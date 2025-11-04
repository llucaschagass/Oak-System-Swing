package br.com.controle_estoque.Controle_Estoque.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DashboardPanel extends JPanel {

    public DashboardPanel() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(25, 30, 25, 30));
        setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel lblTitulo = new JLabel("Dashboard");
        lblTitulo.setFont(UIManager.getFont("h1.font"));
        headerPanel.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblSubtitulo = new JLabel("Bem-vindo ao seu painel de controle de estoque!");
        lblSubtitulo.setFont(UIManager.getFont("large.font"));
        lblSubtitulo.setForeground(Color.GRAY);
        headerPanel.add(lblSubtitulo, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        JPanel kpiGridPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        kpiGridPanel.setBackground(Color.WHITE);

        // Dados estáticos (mockados)
        kpiGridPanel.add(createKpiCard("Produtos Cadastrados", "152", new Color(0, 150, 136)));
        kpiGridPanel.add(createKpiCard("Valor Total do Estoque", "R$ 15.780,50", new Color(255, 152, 0)));
        kpiGridPanel.add(createKpiCard("Produtos Abaixo do Mínimo", "12", new Color(244, 67, 54)));
        kpiGridPanel.add(createKpiCard("Movimentações (Hoje)", "34", new Color(33, 150, 243)));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(kpiGridPanel, BorderLayout.NORTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createKpiCard(String titulo, String valor, Color corIndicador) {
        JPanel card = new JPanel(new BorderLayout(0, 5));
        card.setBackground(Color.WHITE);

        card.setPreferredSize(new Dimension(250, 120));

        Border sombra = BorderFactory.createMatteBorder(1, 1, 1, 1, new Color(0,0,0,50));
        Border indicador = BorderFactory.createMatteBorder(0, 5, 0, 0, corIndicador);
        Border padding = new EmptyBorder(15, 15, 15, 15);

        card.setBorder(BorderFactory.createCompoundBorder(sombra,
                BorderFactory.createCompoundBorder(indicador, padding)));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(UIManager.getFont("h4.font"));
        lblTitulo.setForeground(Color.DARK_GRAY);
        card.add(lblTitulo, BorderLayout.NORTH);

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(UIManager.getFont("h1.font").deriveFont(32f));
        lblValor.setForeground(Color.BLACK);
        card.add(lblValor, BorderLayout.CENTER);

        return card;
    }
}