package com.itla.nomina.gui;

import com.itla.nomina.contrato.Bonificable;
import com.itla.nomina.excepciones.DatoInvalidoException;
import com.itla.nomina.modelo.Departamento;
import com.itla.nomina.modelo.Empleado;
import com.itla.nomina.modelo.EmpleadoAsalariado;
import com.itla.nomina.modelo.EmpleadoPorHora;
import com.itla.nomina.modelo.Freelance;
import com.itla.nomina.servicio.GestorNomina;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Interfaz grafica (Swing) para el Sistema de Gestion de Nomina.
 *
 * <p>Es una capa de presentacion alternativa al menu de consola ({@code Main}).
 * No duplica logica de negocio: reutiliza {@link GestorNomina} y las clases
 * del modelo tal cual, por lo que sigue demostrando los mismos 4 pilares de
 * la POO (la GUI solo agrega/lista/busca a traves del gestor).</p>
 *
 * <p>La ventana esta organizada por <b>apartados</b> (Dashboard, Registrar,
 * Empleados y Buscar) navegables desde una barra lateral, en lugar de mostrar
 * todos los controles amontonados a la vez.</p>
 */
public class NominaGUI extends JFrame {

    private static final String SEC_DASHBOARD = "dashboard";
    private static final String SEC_REGISTRAR = "registrar";
    private static final String SEC_EMPLEADOS = "empleados";
    private static final String SEC_BUSCAR = "buscar";

    private final GestorNomina gestor = new GestorNomina();
    private final NominaTableModel tableModel = new NominaTableModel();
    private final JTable tabla = new JTable(tableModel);

    private final NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.of("es", "DO"));

    // Navegacion
    private final CardLayout contenidoCard = new CardLayout();
    private final JPanel contenidoPanel = new JPanel(contenidoCard);
    private final Map<String, JButton> botonesNav = new LinkedHashMap<>();
    private JButton navSeleccionado;

    // Dashboard
    private final StatCard cardEmpleados = new StatCard("👥", "Empleados registrados", Tema.PRIMARIO);
    private final StatCard cardBonos = new StatCard("🎁", "Total en bonos", Tema.AMBAR);
    private final StatCard cardNomina = new StatCard("💰", "Nomina total", Tema.EXITO);
    private final JLabel dashboardVacio = new JLabel();

    // Formulario de registro
    private final JComboBox<String> tipoCombo = new JComboBox<>(new String[]{"Asalariado", "Por Hora", "Freelance"});
    private final JTextField nombreField = Tema.campoTexto();
    private final JTextField cedulaField = Tema.campoTexto();
    private final JComboBox<Departamento> departamentoCombo = new JComboBox<>(Departamento.values());

    private final CardLayout camposCard = new CardLayout();
    private final JPanel camposPanel = new JPanel(camposCard);

    private final JTextField salarioField = Tema.campoTexto();
    private final JTextField tarifaField = Tema.campoTexto();
    private final JTextField horasField = Tema.campoTexto();
    private final JTextField proyectosField = Tema.campoTexto();
    private final JTextField pagoProyectoField = Tema.campoTexto();

    // Empleados (tabla)
    private final JLabel contadorTablaLabel = new JLabel();

    // Buscar
    private final JTextField busquedaField = Tema.campoTexto();
    private final JPanel resultadoBusquedaPanel = new JPanel(new BorderLayout());

    public NominaGUI() {
        super("Sistema de Gestion de Nomina - ITLA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(Tema.FONDO);
        setLayout(new BorderLayout());

        add(construirBarraLateral(), BorderLayout.WEST);
        add(construirContenido(), BorderLayout.CENTER);

        cargarDatosDeEjemplo();
        mostrarSeccion(SEC_DASHBOARD);

        setMinimumSize(new Dimension(1050, 620));
        setSize(new Dimension(1100, 680));
        setLocationRelativeTo(null);
    }

    // ─────────────────────────────────────────────────────────────
    //  BARRA LATERAL DE NAVEGACION
    // ─────────────────────────────────────────────────────────────
    private JComponent construirBarraLateral() {
        JPanel barra = new JPanel();
        barra.setLayout(new BoxLayout(barra, BoxLayout.Y_AXIS));
        barra.setBackground(Tema.BARRA_LATERAL);
        barra.setBorder(new EmptyBorder(20, 14, 20, 14));
        barra.setPreferredSize(new Dimension(210, 0));

        JLabel marca = new JLabel("NOMINA ITLA");
        marca.setFont(Tema.fuente(Font.BOLD, 16));
        marca.setForeground(Color.WHITE);
        marca.setAlignmentX(Component.LEFT_ALIGNMENT);
        marca.setBorder(new EmptyBorder(4, 6, 24, 6));
        barra.add(marca);

        barra.add(botonNav(SEC_DASHBOARD, "📊  Dashboard"));
        barra.add(Box.createVerticalStrut(6));
        barra.add(botonNav(SEC_REGISTRAR, "➕  Registrar empleado"));
        barra.add(Box.createVerticalStrut(6));
        barra.add(botonNav(SEC_EMPLEADOS, "👥  Empleados"));
        barra.add(Box.createVerticalStrut(6));
        barra.add(botonNav(SEC_BUSCAR, "🔍  Buscar"));

        barra.add(Box.createVerticalGlue());

        JButton recargarBtn = new JButton("↻  Recargar datos de ejemplo");
        recargarBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        recargarBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        recargarBtn.setFont(Tema.fuente(Font.PLAIN, 12));
        recargarBtn.setForeground(Tema.BARRA_LATERAL_TEXTO);
        recargarBtn.setBackground(Tema.BARRA_LATERAL);
        recargarBtn.setBorder(new EmptyBorder(8, 8, 8, 8));
        recargarBtn.setFocusPainted(false);
        recargarBtn.setContentAreaFilled(false);
        recargarBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        recargarBtn.addActionListener(e -> {
            gestor.limpiar();
            cargarDatosDeEjemplo();
        });
        barra.add(recargarBtn);

        return barra;
    }

    private JButton botonNav(String seccion, String texto) {
        JButton boton = new JButton(texto);
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setFont(Tema.fuente(Font.BOLD, 13));
        boton.setForeground(Tema.BARRA_LATERAL_TEXTO);
        boton.setBackground(Tema.BARRA_LATERAL);
        boton.setOpaque(true);
        boton.setBorder(new EmptyBorder(10, 12, 10, 12));
        boton.setFocusPainted(false);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.addActionListener(e -> mostrarSeccion(seccion));
        botonesNav.put(seccion, boton);
        return boton;
    }

    private void mostrarSeccion(String seccion) {
        if (navSeleccionado != null) {
            navSeleccionado.setBackground(Tema.BARRA_LATERAL);
            navSeleccionado.setForeground(Tema.BARRA_LATERAL_TEXTO);
        }
        JButton boton = botonesNav.get(seccion);
        if (boton != null) {
            boton.setBackground(Tema.PRIMARIO);
            boton.setForeground(Color.WHITE);
            navSeleccionado = boton;
        }
        if (SEC_DASHBOARD.equals(seccion)) {
            actualizarDashboard();
        }
        contenidoCard.show(contenidoPanel, seccion);
    }

    // ─────────────────────────────────────────────────────────────
    //  CONTENEDOR PRINCIPAL (apartados)
    // ─────────────────────────────────────────────────────────────
    private JComponent construirContenido() {
        contenidoPanel.setBackground(Tema.FONDO);
        contenidoPanel.add(envolver(construirDashboard()), SEC_DASHBOARD);
        contenidoPanel.add(envolver(construirFormularioRegistro()), SEC_REGISTRAR);
        contenidoPanel.add(envolver(construirSeccionEmpleados()), SEC_EMPLEADOS);
        contenidoPanel.add(envolver(construirSeccionBuscar()), SEC_BUSCAR);
        return contenidoPanel;
    }

    private JComponent envolver(JComponent contenido) {
        JPanel externo = new JPanel(new BorderLayout());
        externo.setBackground(Tema.FONDO);
        externo.setBorder(new EmptyBorder(24, 28, 24, 28));
        externo.add(contenido, BorderLayout.CENTER);
        return externo;
    }

    // ─────────────────────────────────────────────────────────────
    //  APARTADO 1: DASHBOARD
    // ─────────────────────────────────────────────────────────────
    private JComponent construirDashboard() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        encabezado.add(Tema.titulo("Dashboard"));
        encabezado.add(Tema.subtitulo("Resumen general de la nomina de la empresa"));
        panel.add(encabezado, BorderLayout.NORTH);

        JPanel tarjetas = new JPanel(new GridLayout(1, 3, 18, 0));
        tarjetas.setOpaque(false);
        tarjetas.setBorder(new EmptyBorder(10, 0, 0, 0));
        tarjetas.add(cardEmpleados);
        tarjetas.add(cardBonos);
        tarjetas.add(cardNomina);

        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(tarjetas, BorderLayout.NORTH);

        JPanel accesos = Tema.tarjeta();
        accesos.setLayout(new BoxLayout(accesos, BoxLayout.Y_AXIS));
        JLabel accesosTitulo = new JLabel("Acciones rapidas");
        accesosTitulo.setFont(Tema.fuente(Font.BOLD, 15));
        accesosTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        accesos.add(accesosTitulo);
        accesos.add(Box.createVerticalStrut(12));

        JButton irRegistrar = Tema.botonPrimario("➕  Registrar nuevo empleado");
        irRegistrar.setAlignmentX(Component.LEFT_ALIGNMENT);
        irRegistrar.addActionListener(e -> mostrarSeccion(SEC_REGISTRAR));
        accesos.add(irRegistrar);
        accesos.add(Box.createVerticalStrut(10));

        JButton irEmpleados = Tema.botonSecundario("👥  Ver todos los empleados");
        irEmpleados.setAlignmentX(Component.LEFT_ALIGNMENT);
        irEmpleados.addActionListener(e -> mostrarSeccion(SEC_EMPLEADOS));
        accesos.add(irEmpleados);

        JPanel accesosContenedor = new JPanel(new BorderLayout());
        accesosContenedor.setOpaque(false);
        accesosContenedor.setBorder(new EmptyBorder(24, 0, 0, 0));
        accesosContenedor.add(accesos, BorderLayout.NORTH);
        centro.add(accesosContenedor, BorderLayout.CENTER);

        panel.add(centro, BorderLayout.CENTER);
        return panel;
    }

    private void actualizarDashboard() {
        cardEmpleados.setValor(String.valueOf(gestor.cantidad()));
        cardBonos.setValor(moneda.format(gestor.calcularTotalBonos()));
        cardNomina.setValor(moneda.format(gestor.calcularNominaTotal()));
    }

    // ─────────────────────────────────────────────────────────────
    //  APARTADO 2: REGISTRAR EMPLEADO
    // ─────────────────────────────────────────────────────────────
    private JComponent construirFormularioRegistro() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        encabezado.add(Tema.titulo("Registrar empleado"));
        encabezado.add(Tema.subtitulo("Complete los datos segun el tipo de empleado seleccionado"));
        panel.add(encabezado, BorderLayout.NORTH);

        JPanel tarjeta = Tema.tarjeta();
        tarjeta.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        int fila = 0;
        fila = agregarCampo(tarjeta, gbc, fila, "Tipo de empleado", tipoCombo);
        fila = agregarCampo(tarjeta, gbc, fila, "Nombre completo", nombreField);
        fila = agregarCampo(tarjeta, gbc, fila, "Cedula", cedulaField);
        fila = agregarCampo(tarjeta, gbc, fila, "Departamento", departamentoCombo);

        departamentoCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                            boolean isSelected, boolean cellHasFocus) {
                Object mostrar = (value instanceof Departamento d) ? d.getNombreLegible() : value;
                return super.getListCellRendererComponent(list, mostrar, index, isSelected, cellHasFocus);
            }
        });
        Tema.estilizarCampo((JComponent) departamentoCombo.getEditor().getEditorComponent());
        tipoCombo.setFont(Tema.fuente(Font.PLAIN, 13));
        departamentoCombo.setFont(Tema.fuente(Font.PLAIN, 13));

        camposPanel.setOpaque(false);
        camposPanel.add(construirCamposAsalariado(), "Asalariado");
        camposPanel.add(construirCamposPorHora(), "Por Hora");
        camposPanel.add(construirCamposFreelance(), "Freelance");

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        tarjeta.add(camposPanel, gbc);
        fila++;

        tipoCombo.addActionListener(e -> camposCard.show(camposPanel, (String) tipoCombo.getSelectedItem()));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        botones.setOpaque(false);
        JButton registrarBtn = Tema.botonPrimario("Registrar empleado");
        registrarBtn.addActionListener(e -> registrarEmpleado());
        JButton limpiarBtn = Tema.botonSecundario("Limpiar formulario");
        limpiarBtn.addActionListener(e -> limpiarFormulario());
        botones.add(registrarBtn);
        botones.add(limpiarBtn);

        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 6, 6, 6);
        tarjeta.add(botones, gbc);

        JPanel tarjetaContenedor = new JPanel(new BorderLayout());
        tarjetaContenedor.setOpaque(false);
        tarjetaContenedor.setBorder(new EmptyBorder(4, 0, 0, 0));
        tarjetaContenedor.setMaximumSize(new Dimension(520, 600));
        JPanel alineado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        alineado.setOpaque(false);
        JPanel limitador = new JPanel(new BorderLayout());
        limitador.setOpaque(false);
        limitador.setPreferredSize(new Dimension(480, 430));
        limitador.add(tarjeta, BorderLayout.CENTER);
        alineado.add(limitador);
        panel.add(alineado, BorderLayout.CENTER);

        return panel;
    }

    private int agregarCampo(JPanel panel, GridBagConstraints gbc, int fila, String etiqueta, JComponent campo) {
        gbc.gridx = 0;
        gbc.gridy = fila;
        gbc.gridwidth = 2;
        panel.add(Tema.etiquetaCampo(etiqueta), gbc);
        gbc.gridy = fila + 1;
        panel.add(campo, gbc);
        return fila + 2;
    }

    private JPanel construirCamposAsalariado() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridy = 0;
        p.add(Tema.etiquetaCampo("Salario mensual (RD$)"), gbc);
        gbc.gridy = 1;
        p.add(salarioField, gbc);
        return p;
    }

    private JPanel construirCamposPorHora() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridy = 0;
        p.add(Tema.etiquetaCampo("Tarifa por hora (RD$)"), gbc);
        gbc.gridy = 1;
        p.add(tarifaField, gbc);
        gbc.gridy = 2;
        p.add(Tema.etiquetaCampo("Horas trabajadas"), gbc);
        gbc.gridy = 3;
        p.add(horasField, gbc);
        return p;
    }

    private JPanel construirCamposFreelance() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 0, 6, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.gridy = 0;
        p.add(Tema.etiquetaCampo("Proyectos entregados"), gbc);
        gbc.gridy = 1;
        p.add(proyectosField, gbc);
        gbc.gridy = 2;
        p.add(Tema.etiquetaCampo("Pago por proyecto (RD$)"), gbc);
        gbc.gridy = 3;
        p.add(pagoProyectoField, gbc);
        return p;
    }

    // ─────────────────────────────────────────────────────────────
    //  APARTADO 3: EMPLEADOS (tabla)
    // ─────────────────────────────────────────────────────────────
    private JComponent construirSeccionEmpleados() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setOpaque(false);
        JPanel titulos = new JPanel(new GridLayout(2, 1));
        titulos.setOpaque(false);
        titulos.add(Tema.titulo("Empleados"));
        titulos.add(Tema.subtitulo("Listado completo con salario y bono calculados"));
        encabezado.add(titulos, BorderLayout.WEST);

        JButton eliminarBtn = Tema.botonPeligro("Eliminar seleccionado");
        eliminarBtn.addActionListener(e -> eliminarSeleccionado());
        JPanel accionesEncabezado = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        accionesEncabezado.setOpaque(false);
        accionesEncabezado.add(eliminarBtn);
        encabezado.add(accionesEncabezado, BorderLayout.EAST);

        panel.add(encabezado, BorderLayout.NORTH);

        JPanel tarjetaTabla = Tema.tarjeta();
        tarjetaTabla.setLayout(new BorderLayout(0, 10));

        estilizarTabla();
        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setBorder(BorderFactory.createLineBorder(Tema.BORDE));
        tarjetaTabla.add(scroll, BorderLayout.CENTER);

        contadorTablaLabel.setFont(Tema.fuente(Font.PLAIN, 12));
        contadorTablaLabel.setForeground(Tema.TEXTO_SUAVE);
        tarjetaTabla.add(contadorTablaLabel, BorderLayout.SOUTH);

        panel.add(tarjetaTabla, BorderLayout.CENTER);
        return panel;
    }

    private void estilizarTabla() {
        tabla.setRowHeight(30);
        tabla.setShowGrid(false);
        tabla.setIntercellSpacing(new Dimension(0, 0));
        tabla.setSelectionBackground(Tema.PRIMARIO_SUAVE);
        tabla.setSelectionForeground(Tema.TEXTO);
        tabla.setFont(Tema.fuente(Font.PLAIN, 13));
        tabla.setFillsViewportHeight(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JTableHeader header = tabla.getTableHeader();
        header.setFont(Tema.fuente(Font.BOLD, 12));
        header.setBackground(new Color(250, 250, 252));
        header.setForeground(Tema.TEXTO_SUAVE);
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                             boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!isSelected) {
                    setBackground(row % 2 == 0 ? Color.WHITE : new Color(250, 250, 252));
                }
                return c;
            }
        };
        for (int i = 0; i < tabla.getColumnCount(); i++) {
            tabla.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int fila = tabla.getSelectedRow();
                if (fila >= 0) {
                    cedulaField.setText(tableModel.getEmpleadoEn(fila).getCedula());
                }
            }
        });
    }

    // ─────────────────────────────────────────────────────────────
    //  APARTADO 4: BUSCAR
    // ─────────────────────────────────────────────────────────────
    private JComponent construirSeccionBuscar() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setOpaque(false);

        JPanel encabezado = new JPanel(new GridLayout(2, 1));
        encabezado.setOpaque(false);
        encabezado.add(Tema.titulo("Buscar empleado"));
        encabezado.add(Tema.subtitulo("Busque un empleado especifico por su numero de cedula"));
        panel.add(encabezado, BorderLayout.NORTH);

        JPanel tarjeta = Tema.tarjeta();
        tarjeta.setLayout(new BorderLayout(0, 16));

        JPanel filaBusqueda = new JPanel(new BorderLayout(10, 0));
        filaBusqueda.setOpaque(false);
        busquedaField.addActionListener(e -> buscarEmpleado());
        filaBusqueda.add(busquedaField, BorderLayout.CENTER);
        JButton buscarBtn = Tema.botonPrimario("Buscar");
        buscarBtn.addActionListener(e -> buscarEmpleado());
        filaBusqueda.add(buscarBtn, BorderLayout.EAST);
        tarjeta.add(filaBusqueda, BorderLayout.NORTH);

        resultadoBusquedaPanel.setOpaque(false);
        mostrarResultadoBusquedaVacio();
        tarjeta.add(resultadoBusquedaPanel, BorderLayout.CENTER);

        JPanel limitador = new JPanel(new BorderLayout());
        limitador.setOpaque(false);
        limitador.setPreferredSize(new Dimension(560, 260));
        JPanel alineado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        alineado.setOpaque(false);
        limitador.add(tarjeta, BorderLayout.CENTER);
        alineado.add(limitador);
        panel.add(alineado, BorderLayout.CENTER);

        return panel;
    }

    private void mostrarResultadoBusquedaVacio() {
        resultadoBusquedaPanel.removeAll();
        JLabel vacio = new JLabel("Escriba una cedula y presione Buscar.");
        vacio.setFont(Tema.fuente(Font.PLAIN, 13));
        vacio.setForeground(Tema.TEXTO_SUAVE);
        resultadoBusquedaPanel.add(vacio, BorderLayout.NORTH);
        resultadoBusquedaPanel.revalidate();
        resultadoBusquedaPanel.repaint();
    }

    // ─────────────────────────────────────────────────────────────
    //  ACCIONES
    // ─────────────────────────────────────────────────────────────
    private void registrarEmpleado() {
        String nombre = nombreField.getText().trim();
        String cedula = cedulaField.getText().trim();
        Departamento departamento = (Departamento) departamentoCombo.getSelectedItem();
        String tipo = (String) tipoCombo.getSelectedItem();

        try {
            Empleado empleado = switch (tipo) {
                case "Asalariado" -> new EmpleadoAsalariado(
                        nombre, cedula, departamento, leerDecimal(salarioField, "Salario mensual"));
                case "Por Hora" -> new EmpleadoPorHora(
                        nombre, cedula, departamento,
                        leerDecimal(tarifaField, "Tarifa por hora"),
                        (int) leerDecimal(horasField, "Horas trabajadas"));
                default -> new Freelance(
                        nombre, cedula, departamento,
                        (int) leerDecimal(proyectosField, "Proyectos entregados"),
                        leerDecimal(pagoProyectoField, "Pago por proyecto"));
            };

            gestor.agregar(empleado);
            refrescarDatos();
            limpiarFormulario();
            JOptionPane.showMessageDialog(this,
                    "Empleado registrado correctamente:\n" + empleado,
                    "Registro exitoso", JOptionPane.INFORMATION_MESSAGE);
            mostrarSeccion(SEC_EMPLEADOS);
        } catch (DatoInvalidoException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Datos invalidos", JOptionPane.ERROR_MESSAGE);
        }
    }

    private double leerDecimal(JTextField campo, String etiqueta) {
        String texto = campo.getText().trim().replace(",", ".");
        if (texto.isEmpty()) {
            throw new NumberFormatException(etiqueta + " no puede estar vacio.");
        }
        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(etiqueta + " debe ser un numero valido.");
        }
    }

    private void limpiarFormulario() {
        nombreField.setText("");
        cedulaField.setText("");
        salarioField.setText("");
        tarifaField.setText("");
        horasField.setText("");
        proyectosField.setText("");
        pagoProyectoField.setText("");
        nombreField.requestFocus();
    }

    private void buscarEmpleado() {
        String cedula = busquedaField.getText().trim();
        resultadoBusquedaPanel.removeAll();

        if (cedula.isEmpty()) {
            mostrarResultadoBusquedaVacio();
            return;
        }

        Optional<Empleado> resultado = gestor.buscarPorCedula(cedula);
        if (resultado.isPresent()) {
            resultadoBusquedaPanel.add(construirTarjetaResultado(resultado.get()), BorderLayout.NORTH);
        } else {
            JLabel noEncontrado = new JLabel("No se encontro ningun empleado con esa cedula.");
            noEncontrado.setFont(Tema.fuente(Font.PLAIN, 13));
            noEncontrado.setForeground(Tema.PELIGRO);
            resultadoBusquedaPanel.add(noEncontrado, BorderLayout.NORTH);
        }
        resultadoBusquedaPanel.revalidate();
        resultadoBusquedaPanel.repaint();
    }

    private JComponent construirTarjetaResultado(Empleado e) {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 6));
        panel.setOpaque(false);

        JLabel nombreLabel = new JLabel(e.getNombre() + "  (" + e.getTipo() + ")");
        nombreLabel.setFont(Tema.fuente(Font.BOLD, 16));
        panel.add(nombreLabel);

        panel.add(filaDetalle("Cedula", e.getCedula()));
        panel.add(filaDetalle("Departamento", e.getDepartamento().getNombreLegible()));
        panel.add(filaDetalle("Salario calculado", moneda.format(e.calcularSalario())));

        if (e instanceof Bonificable b) {
            panel.add(filaDetalle("Bono", moneda.format(b.calcularBono())));
        } else {
            panel.add(filaDetalle("Bono", "Este empleado no recibe bono"));
        }

        seleccionarFilaDe(e);
        return panel;
    }

    private JComponent filaDetalle(String etiqueta, String valor) {
        JPanel fila = new JPanel(new BorderLayout());
        fila.setOpaque(false);
        JLabel et = new JLabel(etiqueta + ":");
        et.setFont(Tema.fuente(Font.PLAIN, 12));
        et.setForeground(Tema.TEXTO_SUAVE);
        JLabel val = new JLabel(valor);
        val.setFont(Tema.fuente(Font.BOLD, 13));
        fila.add(et, BorderLayout.WEST);
        fila.add(val, BorderLayout.CENTER);
        return fila;
    }

    private void seleccionarFilaDe(Empleado empleado) {
        List<Empleado> lista = gestor.getEmpleados();
        int indice = lista.indexOf(empleado);
        if (indice >= 0) {
            tabla.setRowSelectionInterval(indice, indice);
        }
    }

    private void eliminarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un empleado en la tabla primero.",
                    "Eliminar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Empleado empleado = tableModel.getEmpleadoEn(fila);
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Eliminar a " + empleado.getNombre() + "?",
                "Confirmar eliminacion", JOptionPane.YES_NO_OPTION);
        if (confirmacion == JOptionPane.YES_OPTION) {
            gestor.eliminarPorCedula(empleado.getCedula());
            refrescarDatos();
        }
    }

    /** Refresca tabla, dashboard y contador tras cualquier cambio en el gestor. */
    private void refrescarDatos() {
        tableModel.refrescar();
        actualizarDashboard();
        contadorTablaLabel.setText(gestor.cantidad() + " empleado(s) registrado(s)");
    }

    private void cargarDatosDeEjemplo() {
        gestor.agregar(new EmpleadoAsalariado(
                "Ana Martinez", "001-1111111-1", Departamento.FINANZAS, 65000));
        gestor.agregar(new EmpleadoPorHora(
                "Carlos Reyes", "002-2222222-2", Departamento.TECNOLOGIA, 450, 170));
        gestor.agregar(new Freelance(
                "Diana Castillo", "003-3333333-3", Departamento.VENTAS, 4, 15000));
        gestor.agregar(new EmpleadoAsalariado(
                "Luis Peralta", "004-4444444-4", Departamento.OPERACIONES, 52000));
        refrescarDatos();
    }

    // ─────────────────────────────────────────────────────────────
    //  MODELO DE TABLA
    // ─────────────────────────────────────────────────────────────
    private final class NominaTableModel extends AbstractTableModel {

        private final String[] columnas = {"Tipo", "Nombre", "Cedula", "Departamento", "Salario", "Bono"};

        void refrescar() {
            fireTableDataChanged();
        }

        Empleado getEmpleadoEn(int fila) {
            return gestor.getEmpleados().get(fila);
        }

        @Override
        public int getRowCount() {
            return gestor.cantidad();
        }

        @Override
        public int getColumnCount() {
            return columnas.length;
        }

        @Override
        public String getColumnName(int column) {
            return columnas[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Empleado e = getEmpleadoEn(rowIndex);
            return switch (columnIndex) {
                case 0 -> e.getTipo();
                case 1 -> e.getNombre();
                case 2 -> e.getCedula();
                case 3 -> e.getDepartamento().getNombreLegible();
                case 4 -> moneda.format(e.calcularSalario());
                case 5 -> (e instanceof Bonificable b) ? moneda.format(b.calcularBono()) : "—";
                default -> "";
            };
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  PUNTO DE ENTRADA
    // ─────────────────────────────────────────────────────────────
    public static void main(String[] args) {
        // El Look&Feel del sistema (Windows) ignora los colores personalizados
        // de los botones (fondo/texto quedan invisibles). Nimbus si respeta
        // setBackground/setForeground, por lo que el tema de Tema.java se ve
        // correctamente en cualquier sistema operativo.
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ignored) {
            // Si Nimbus no esta disponible, se usa el look and feel por defecto de Swing.
        }
        SwingUtilities.invokeLater(() -> new NominaGUI().setVisible(true));
    }
}
