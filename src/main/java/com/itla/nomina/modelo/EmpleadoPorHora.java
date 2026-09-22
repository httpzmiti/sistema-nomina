package com.itla.nomina.modelo;

import com.itla.nomina.contrato.Bonificable;
import com.itla.nomina.excepciones.DatoInvalidoException;

/**
 * Empleado pagado por horas trabajadas, con horas extra.
 *
 * <p>Refuerza el <b>POLIMORFISMO</b>: aunque hereda de {@link Empleado} igual
 * que {@link EmpleadoAsalariado}, su {@link #calcularSalario()} es totalmente
 * distinto (incluye tarifa por hora y recargo por horas extra).</p>
 */
public class EmpleadoPorHora extends Empleado implements Bonificable {

    /** Jornada mensual estandar antes de considerar horas extra. */
    private static final int HORAS_JORNADA_ESTANDAR = 160;

    /** Factor de recargo de las horas extra (1.5 = 50% adicional). */
    private static final double FACTOR_HORA_EXTRA = 1.5;

    private double tarifaPorHora;
    private int horasTrabajadas;

    public EmpleadoPorHora(String nombre, String cedula, Departamento departamento,
                           double tarifaPorHora, int horasTrabajadas) {
        super(nombre, cedula, departamento);
        setTarifaPorHora(tarifaPorHora);
        setHorasTrabajadas(horasTrabajadas);
    }

    /**
     * Regla propia: horas normales + horas extra (recargadas) + bono.
     */
    @Override
    public double calcularSalario() {
        int horasNormales = Math.min(horasTrabajadas, HORAS_JORNADA_ESTANDAR);
        int horasExtra = Math.max(0, horasTrabajadas - HORAS_JORNADA_ESTANDAR);

        double pagoNormal = horasNormales * tarifaPorHora;
        double pagoExtra = horasExtra * tarifaPorHora * FACTOR_HORA_EXTRA;

        return pagoNormal + pagoExtra + calcularBono();
    }

    /** Bono de RD$ 2,000 solo si supero la jornada estandar. */
    @Override
    public double calcularBono() {
        return horasTrabajadas > HORAS_JORNADA_ESTANDAR ? 2000.0 : 0.0;
    }

    @Override
    public String getTipo() {
        return "Por Hora";
    }

    // ── Encapsulamiento ──
    public double getTarifaPorHora() {
        return tarifaPorHora;
    }

    public void setTarifaPorHora(double tarifaPorHora) {
        if (tarifaPorHora <= 0) {
            throw new DatoInvalidoException("La tarifa por hora debe ser mayor que cero.");
        }
        this.tarifaPorHora = tarifaPorHora;
    }

    public int getHorasTrabajadas() {
        return horasTrabajadas;
    }

    public void setHorasTrabajadas(int horasTrabajadas) {
        if (horasTrabajadas < 0) {
            throw new DatoInvalidoException("Las horas trabajadas no pueden ser negativas.");
        }
        this.horasTrabajadas = horasTrabajadas;
    }
}
