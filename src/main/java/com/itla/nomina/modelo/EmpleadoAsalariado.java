package com.itla.nomina.modelo;

import com.itla.nomina.contrato.Bonificable;
import com.itla.nomina.excepciones.DatoInvalidoException;

/**
 * Empleado con salario fijo mensual.
 *
 * <p>Demuestra:</p>
 * <ul>
 *   <li><b>HERENCIA</b>: {@code extends Empleado} (ES UN empleado).</li>
 *   <li><b>Interfaz</b>: {@code implements Bonificable} (PUEDE recibir bono).</li>
 *   <li><b>POLIMORFISMO</b>: sobrescribe {@link #calcularSalario()} con SU regla.</li>
 * </ul>
 */
public class EmpleadoAsalariado extends Empleado implements Bonificable {

    private double salarioMensual;

    public EmpleadoAsalariado(String nombre, String cedula,
                              Departamento departamento, double salarioMensual) {
        // Llamamos al constructor del padre para inicializar lo comun
        super(nombre, cedula, departamento);
        setSalarioMensual(salarioMensual);
    }

    /** Regla propia: salario base + su bono. */
    @Override
    public double calcularSalario() {
        return salarioMensual + calcularBono();
    }

    /** Bono fijo del 10% del salario mensual. */
    @Override
    public double calcularBono() {
        return salarioMensual * 0.10;
    }

    @Override
    public String getTipo() {
        return "Asalariado";
    }

    // ── Encapsulamiento ──
    public double getSalarioMensual() {
        return salarioMensual;
    }

    public void setSalarioMensual(double salarioMensual) {
        if (salarioMensual < 0) {
            throw new DatoInvalidoException("El salario mensual no puede ser negativo.");
        }
        this.salarioMensual = salarioMensual;
    }
}
