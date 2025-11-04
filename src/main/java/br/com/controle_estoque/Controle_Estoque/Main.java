package br.com.controle_estoque.Controle_Estoque;

import br.com.controle_estoque.Controle_Estoque.view.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        try {
            FlatLightLaf.setup();
        } catch (Exception ex) {
            System.err.println("Falha ao inicializar o Look and Feel (FlatLaf).");
        }

        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}