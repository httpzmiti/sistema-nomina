package com.itla.nomina.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Tarjeta de resumen para el panel de Dashboard (icono + etiqueta + valor).
 *
 * <p>Componente puramente visual: no conoce nada de {@code Empleado} ni de
 * {@code GestorNomina}, solo expone {@link #setValor(String)} para que
 * {@link NominaGUI} la actualice cuando cambian los datos.</p>
 */
final class StatCard extends JPanel {

    private final JLabel valorLabel;

    StatCard(String icono, String etiqueta, Color acento) {
        setLayout(new BorderLayout(4, 6));
        setOpaque(false);

        Tema.RoundedPanel fondo = new Tema.RoundedPanel(14);
        fondo.setBackground(Tema.TARJETA);
        fondo.setLayout(new BorderLayout(4, 6));
        fondo.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);

        JLabel iconoLabel = new JLabel(icono);
        iconoLabel.setFont(Tema.fuente(Font.PLAIN, 22));
        encabezado.add(iconoLabel, BorderLayout.WEST);

        JPanel barraAcento = new JPanel();
        barraAcento.setPreferredSize(new Dimension(10, 10));
        barraAcento.setBackground(acento);
        JPanel contenedorBarra = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        contenedorBarra.setOpaque(false);
        contenedorBarra.add(barraAcento);
        encabezado.add(contenedorBarra, BorderLayout.EAST);

        JLabel etiquetaLabel = new JLabel(etiqueta);
        etiquetaLabel.setFont(Tema.fuente(Font.BOLD, 12));
        etiquetaLabel.setForeground(Tema.TEXTO_SUAVE);

        valorLabel = new JLabel("—");
        valorLabel.setFont(Tema.fuente(Font.BOLD, 22));
        valorLabel.setForeground(Tema.TEXTO);

        fondo.add(encabezado, BorderLayout.NORTH);
        fondo.add(valorLabel, BorderLayout.CENTER);
        fondo.add(etiquetaLabel, BorderLayout.SOUTH);

        add(fondo, BorderLayout.CENTER);
    }

    void setValor(String texto) {
        valorLabel.setText(texto);
    }
}
