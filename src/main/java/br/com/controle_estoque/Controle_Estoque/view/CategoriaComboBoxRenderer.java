package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.dto.CategoriaDTO;
import javax.swing.*;
import java.awt.*;

/**
 * Renderizador customizado para exibir objetos {@link CategoriaDTO} em um JComboBox.
 * Garante que o JComboBox mostre o nome da categoria, em vez da referência do objeto.
 */
public class CategoriaComboBoxRenderer extends DefaultListCellRenderer {

    /**
     * Configura o componente para exibir o nome da categoria.
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        // Chama a implementação padrão para obter o estilo (cores de seleção, etc.)
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

        // Verifica se o item é um CategoriaDTO
        if (value instanceof CategoriaDTO) {
            CategoriaDTO categoria = (CategoriaDTO) value;
            // Define o texto do item para ser o nome da categoria
            setText(categoria.getNome());
        }
        return this;
    }
}