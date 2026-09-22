package com.itla.nomina;

import com.itla.nomina.contrato.Bonificable;
import com.itla.nomina.excepciones.DatoInvalidoException;
import com.itla.nomina.modelo.Departamento;
import com.itla.nomina.modelo.Empleado;
import com.itla.nomina.modelo.EmpleadoAsalariado;
import com.itla.nomina.modelo.EmpleadoPorHora;
import com.itla.nomina.modelo.Freelance;
import com.itla.nomina.servicio.GestorNomina;
import com.itla.nomina.util.ConsolaUtil;

import java.util.Optional;
import java.util.Scanner;

/**
 * Punto de entrada de la aplicacion: menu por consola.
 *
 * <p>Esta clase solo se encarga de la INTERACCION con el usuario. Toda la
 * logica vive en {@link GestorNomina} y en las clases del modelo. Asi cada
 * pieza tiene una unica responsabilidad.</p>
 */
public class Main {

    private final GestorNomina gestor = new GestorNomina();
    private final ConsolaUtil consola;
    private final Scanner scanner;

    public Main(Scanner scanner) {
        this.scanner = scanner;
        this.consola = new ConsolaUtil(scanner);
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            new Main(scanner).ejecutar();
        }
    }

    /** Bucle principal del menu. */
    public void ejecutar() {
        cargarDatosDeEjemplo(); // arrancamos con datos para probar rapido
        System.out.println("""
                ============================================================
                         SISTEMA DE GESTION DE NOMINA - ITLA
                   Demostracion de los 4 pilares de la POO (Java 21)
                ============================================================""");

        boolean continuar = true;
        while (continuar) {
            mostrarMenu();
            int opcion = consola.leerEnteroEnRango("Seleccione una opcion: ", 0, 5);
            System.out.println();

            switch (opcion) {
                case 1 -> registrarEmpleado();
                case 2 -> listarEmpleados();
                case 3 -> mostrarNominaTotal();
                case 4 -> buscarEmpleado();
                case 5 -> cargarDatosDeEjemplo();
                case 0 -> {
                    continuar = false;
                    System.out.println("Gracias por usar el sistema. Hasta luego!");
                }
                default -> System.out.println("Opcion no reconocida.");
            }
            System.out.println();
        }
    }

    private void mostrarMenu() {
        System.out.println("""
                ------------------------------------------------------------
                 1. Registrar empleado
                 2. Listar empleados (con salario calculado)
                 3. Ver nomina total y bonos
                 4. Buscar empleado por cedula
                 5. Recargar datos de ejemplo
                 0. Salir
                ------------------------------------------------------------""");
    }

    // ─────────────────────────────────────────────────────────────
    //  OPCION 1: registrar empleado (elige el tipo -> herencia)
    // ─────────────────────────────────────────────────────────────
    private void registrarEmpleado() {
        System.out.println("=== Registrar nuevo empleado ===");
        System.out.println(" 1. Asalariado");
        System.out.println(" 2. Por hora");
        System.out.println(" 3. Freelance");
        int tipo = consola.leerEnteroEnRango("Tipo de empleado: ", 1, 3);

        String nombre = consola.leerTexto("Nombre completo: ");
        String cedula = consola.leerTexto("Cedula: ");
        Departamento departamento = seleccionarDepartamento();

        try {
            // Segun el tipo elegido creamos una subclase distinta.
            // Todas caben en la variable 'empleado' de tipo Empleado (padre):
            // esto es POLIMORFISMO en accion.
            Empleado empleado = switch (tipo) {
                case 1 -> {
                    double salario = consola.leerDecimalPositivo("Salario mensual (RD$): ");
                    yield new EmpleadoAsalariado(nombre, cedula, departamento, salario);
                }
                case 2 -> {
                    double tarifa = consola.leerDecimalPositivo("Tarifa por hora (RD$): ");
                    int horas = consola.leerEnteroEnRango("Horas trabajadas: ", 0, 400);
                    yield new EmpleadoPorHora(nombre, cedula, departamento, tarifa, horas);
                }
                default -> {
                    int proyectos = consola.leerEnteroEnRango("Proyectos entregados: ", 0, 100);
                    double pago = consola.leerDecimalPositivo("Pago por proyecto (RD$): ");
                    yield new Freelance(nombre, cedula, departamento, proyectos, pago);
                }
            };

            gestor.agregar(empleado);
            System.out.println("\n[OK] Empleado registrado correctamente:");
            System.out.println("  " + empleado); // usa el toString() polimorfico
        } catch (DatoInvalidoException e) {
            // Manejo de excepciones: la validacion del encapsulamiento se atrapa aqui.
            System.out.println("\n[ERROR] No se pudo registrar: " + e.getMessage());
        }
    }

    private Departamento seleccionarDepartamento() {
        System.out.println("Departamentos disponibles:");
        Departamento[] valores = Departamento.values();
        for (int i = 0; i < valores.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, valores[i].getNombreLegible());
        }
        int indice = consola.leerEnteroEnRango("Seleccione departamento: ", 1, valores.length);
        return valores[indice - 1];
    }

    // ─────────────────────────────────────────────────────────────
    //  OPCION 2: listar (recorre List<Empleado> polimorficamente)
    // ─────────────────────────────────────────────────────────────
    private void listarEmpleados() {
        System.out.println("=== Lista de empleados (" + gestor.cantidad() + ") ===");
        if (gestor.cantidad() == 0) {
            System.out.println("No hay empleados registrados.");
            return;
        }
        int i = 1;
        for (Empleado e : gestor.getEmpleados()) {
            // No preguntamos "de que tipo eres": cada objeto se imprime solo.
            System.out.printf("%2d. %s%n", i++, e);
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  OPCION 3: totales
    // ─────────────────────────────────────────────────────────────
    private void mostrarNominaTotal() {
        System.out.println("=== Resumen de nomina ===");
        System.out.printf("Empleados registrados : %d%n", gestor.cantidad());
        System.out.printf("Total en bonos        : RD$ %,.2f%n", gestor.calcularTotalBonos());
        System.out.printf("NOMINA TOTAL          : RD$ %,.2f%n", gestor.calcularNominaTotal());
    }

    // ─────────────────────────────────────────────────────────────
    //  OPCION 4: buscar por cedula
    // ─────────────────────────────────────────────────────────────
    private void buscarEmpleado() {
        System.out.println("=== Buscar empleado ===");
        String cedula = consola.leerTexto("Cedula a buscar: ");
        Optional<Empleado> resultado = gestor.buscarPorCedula(cedula);

        if (resultado.isPresent()) {
            Empleado e = resultado.get();
            System.out.println("\n[OK] Empleado encontrado:");
            System.out.println("  " + e);
            // Mostramos el bono solo si el empleado es bonificable.
            if (e instanceof Bonificable bonificable) {
                System.out.println("  " + bonificable.descripcionBono());
            } else {
                System.out.println("  (Este empleado no recibe bono)");
            }
        } else {
            System.out.println("\nNo se encontro ningun empleado con esa cedula.");
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  Datos de ejemplo para probar el sistema sin teclear mucho
    // ─────────────────────────────────────────────────────────────
    private void cargarDatosDeEjemplo() {
        gestor.agregar(new EmpleadoAsalariado(
                "Ana Martinez", "001-1111111-1", Departamento.FINANZAS, 65000));
        gestor.agregar(new EmpleadoPorHora(
                "Carlos Reyes", "002-2222222-2", Departamento.TECNOLOGIA, 450, 170));
        gestor.agregar(new Freelance(
                "Diana Castillo", "003-3333333-3", Departamento.VENTAS, 4, 15000));
        gestor.agregar(new EmpleadoAsalariado(
                "Luis Peralta", "004-4444444-4", Departamento.OPERACIONES, 52000));
        System.out.println("(Datos de ejemplo cargados)");
    }
}
