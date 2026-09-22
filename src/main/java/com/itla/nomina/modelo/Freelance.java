package com.itla.nomina.modelo;

import com.itla.nomina.excepciones.DatoInvalidoException;

/**
 * Empleado freelance: cobra por proyecto entregado.
 *
 * <p>Punto clave para el video: esta clase <b>NO</b> implementa
 * {@code Bonificable}. Es intencional y demuestra buen diseño:
 * no todo empleado recibe bono, asi que no lo forzamos. Aqui se ve por que
 * separamos la HERENCIA (ES UN empleado) de la INTERFAZ (PUEDE recibir bono).</p>
 */
public class Freelance extends Empleado {

    private int proyectosEntregados;
    private double pagoPorProyecto;

    public Freelance(String nombre, String cedula, Departamento departamento,
                     int proyectosEntregados, double pagoPorProyecto) {
        super(nombre, cedula, departamento);
        setProyectosEntregados(proyectosEntregados);
        setPagoPorProyecto(pagoPorProyecto);
    }

    /** Regla propia: pago por proyecto multiplicado por proyectos entregados. */
    @Override
    public double calcularSalario() {
        return proyectosEntregados * pagoPorProyecto;
    }

    @Override
    public String getTipo() {
        return "Freelance";
    }

    // ── Encapsulamiento ──
    public int getProyectosEntregados() {
        return proyectosEntregados;
    }

    public void setProyectosEntregados(int proyectosEntregados) {
        if (proyectosEntregados < 0) {
            throw new DatoInvalidoException("Los proyectos entregados no pueden ser negativos.");
        }
        this.proyectosEntregados = proyectosEntregados;
    }

    public double getPagoPorProyecto() {
        return pagoPorProyecto;
    }

    public void setPagoPorProyecto(double pagoPorProyecto) {
        if (pagoPorProyecto <= 0) {
            throw new DatoInvalidoException("El pago por proyecto debe ser mayor que cero.");
        }
        this.pagoPorProyecto = pagoPorProyecto;
    }
}
