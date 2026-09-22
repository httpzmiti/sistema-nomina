package com.itla.nomina.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Constantes visuales y fabricas de componentes reutilizables para la GUI.
 *
 * <p>Centraliza colores, tipografias y estilos de botones/campos para que
 * todas las secciones de {@link NominaGUI} luzcan consistentes sin repetir
 * codigo (principio DRY aplicado tambien a la capa visual).</p>
 */
final class Tema {

    private Tema() {
    }

    static final Color PRIMARIO = new Color(79, 70, 229);
    static final Color PRIMARIO_HOVER = new Color(67, 56, 202);
    static final Color PRIMARIO_SUAVE = new Color(238, 239, 253);

    static final Color FONDO = new Color(244, 245, 250);
    static final Color TARJETA = Color.WHITE;
    static final Color BORDE = new Color(226, 228, 235);

    static final Color BARRA_LATERAL = new Color(24, 26, 39);
    static final Color BARRA_LATERAL_HOVER = new Color(38, 41, 59);
    static final Color BARRA_LATERAL_TEXTO = new Color(203, 206, 219);

    static final Color TEXTO = new Color(30, 32, 44);
    static final Color TEXTO_SUAVE = new Color(110, 114, 130);

    static final Color EXITO = new Color(22, 163, 74);
    static final Color PELIGRO = new Color(220, 38, 38);
    static final Color PELIGRO_HOVER = new Color(185, 28, 28);
    static final Color AMBAR = new Color(217, 119, 6);

    static final String FUENTE = "Segoe UI";

    static Font fuente(int estilo, int tam) {
        return new Font(FUENTE, estilo, tam);
    }

    static JLabel titulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente(Font.BOLD, 20));
        label.setForeground(TEXTO);
        return label;
    }

    static JLabel subtitulo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente(Font.PLAIN, 13));
        label.setForeground(TEXTO_SUAVE);
        return label;
    }

    static JLabel etiquetaCampo(String texto) {
        JLabel label = new JLabel(texto);
        label.setFont(fuente(Font.BOLD, 12));
        label.setForeground(TEXTO_SUAVE);
        return label;
    }

    /** Botones de accion principal (relleno solido, color de marca). */
    static JButton botonPrimario(String texto) {
        JButton boton = new JButton(texto);
        estilizarBoton(boton, PRIMARIO, PRIMARIO_HOVER, Color.WHITE);
        return boton;
    }

    /** Botones de accion secundaria (borde, sin relleno). */
    static JButton botonSecundario(String texto) {
        JButton boton = new JButton(texto);
        boton.setFont(fuente(Font.BOLD, 12));
        boton.setForeground(TEXTO);
        boton.setBackground(TARJETA);
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(8, 16, 8, 16)));
        boton.addChangeListener(e -> boton.setBackground(
                boton.getModel().isRollover() ? new Color(248, 249, 252) : TARJETA));
        return boton;
    }

    /** Boton de peligro (eliminar, acciones destructivas). */
    static JButton botonPeligro(String texto) {
        JButton boton = new JButton(texto);
        estilizarBoton(boton, PELIGRO, PELIGRO_HOVER, Color.WHITE);
        return boton;
    }

    private static void estilizarBoton(JButton boton, Color base, Color hover, Color texto) {
        boton.setFont(fuente(Font.BOLD, 12));
        boton.setForeground(texto);
        boton.setBackground(base);
        boton.setOpaque(true);
        boton.setFocusPainted(false);
        boton.setBorder(new EmptyBorder(9, 18, 9, 18));
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addChangeListener(e -> boton.setBackground(
                boton.getModel().isRollover() ? hover : base));
    }

    static JTextField campoTexto() {
        JTextField campo = new JTextField();
        estilizarCampo(campo);
        return campo;
    }

    static void estilizarCampo(JComponent campo) {
        campo.setFont(fuente(Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE, 1, true),
                new EmptyBorder(7, 10, 7, 10)));
        campo.setBackground(TARJETA);
    }

    /** Panel tipo "tarjeta": fondo blanco, borde suave y esquinas redondeadas. */
    static JPanel tarjeta() {
        JPanel panel = new RoundedPanel(14);
        panel.setBackground(TARJETA);
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));
        return panel;
    }

    /** JPanel con esquinas redondeadas dibujadas manualmente (sin dependencias externas). */
    static class RoundedPanel extends JPanel {
        private final int radio;

        RoundedPanel(int radio) {
            this.radio = radio;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radio, radio);
            g2.setColor(BORDE);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radio, radio);
            g2.dispose();
            super.paintComponent(g);
        }
    }
}
