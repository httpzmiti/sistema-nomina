package com.itla.nomina.servicio;

import com.itla.nomina.contrato.Bonificable;
import com.itla.nomina.modelo.Empleado;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Servicio que administra la nomina de la empresa.
 *
 * <p>Separa la LOGICA DE NEGOCIO (agregar, buscar, totalizar) de la
 * presentacion (el menu). Esto es "separacion de responsabilidades".</p>
 *
 * <p>Es tambien la mejor demostracion de <b>POLIMORFISMO</b>: guarda una
 * {@code List<Empleado>} y llama a {@code calcularSalario()} SIN saber si
 * cada elemento es Asalariado, PorHora o Freelance. Cada objeto responde
 * con su propia formula. El gestor programa contra la ABSTRACCION (Empleado),
 * no contra los tipos concretos.</p>
 */
public class GestorNomina {

    // La lista es final: la referencia no cambia, pero su contenido si.
    private final List<Empleado> empleados = new ArrayList<>();

    /** Agrega un empleado a la nomina. */
    public void agregar(Empleado empleado) {
        if (empleado == null) {
            throw new IllegalArgumentException("No se puede agregar un empleado nulo.");
        }
        empleados.add(empleado);
    }

    /**
     * Devuelve la lista en modo SOLO LECTURA. Encapsulamiento a nivel de
     * coleccion: nadie de afuera puede agregar/quitar saltandose las reglas.
     */
    public List<Empleado> getEmpleados() {
        return Collections.unmodifiableList(empleados);
    }

    public int cantidad() {
        return empleados.size();
    }

    /**
     * Suma el salario de TODOS los empleados.
     * Usa Streams API: aqui ocurre el polimorfismo en un solo renglon.
     */
    public double calcularNominaTotal() {
        return empleados.stream()
                .mapToDouble(Empleado::calcularSalario) // cada uno calcula distinto
                .sum();
    }

    /**
     * Suma solo los bonos de los empleados que SON bonificables.
     * Demuestra el uso de {@code instanceof} con patron (Java 16+).
     */
    public double calcularTotalBonos() {
        double total = 0.0;
        for (Empleado e : empleados) {
            // Si el empleado implementa Bonificable, lo tratamos como tal.
            if (e instanceof Bonificable bonificable) {
                total += bonificable.calcularBono();
            }
        }
        return total;
    }

    /** Busca un empleado por su cedula (puede no existir -> Optional). */
    public Optional<Empleado> buscarPorCedula(String cedula) {
        return empleados.stream()
                .filter(e -> e.getCedula().equalsIgnoreCase(cedula))
                .findFirst();
    }

    /**
     * Elimina un empleado por su cedula.
     * @return true si se elimino, false si no se encontro.
     */
    public boolean eliminarPorCedula(String cedula) {
        return empleados.removeIf(e -> e.getCedula().equalsIgnoreCase(cedula));
    }

    /** Elimina todos los empleados registrados. */
    public void limpiar() {
        empleados.clear();
    }
}
