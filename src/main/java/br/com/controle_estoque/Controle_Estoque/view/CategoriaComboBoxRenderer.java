package br.com.controle_estoque.Controle_Estoque.view;

import br.com.controle_estoque.Controle_Estoque.dto.CategoriaDTO;
import javax.swing.*;
import java.awt.*;

public class CategoriaComboBoxRenderer extends DefaultListCellRenderer {
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        if (value instanceof CategoriaDTO) {
            CategoriaDTO categoria = (CategoriaDTO) value;
            setText(categoria.getNome());
        }
        return this;
    }
}