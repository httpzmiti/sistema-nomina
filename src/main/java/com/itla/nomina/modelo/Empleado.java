package com.itla.nomina.modelo;

import com.itla.nomina.excepciones.DatoInvalidoException;

/**
 * Clase base ABSTRACTA de todo empleado.
 *
 * <p>Reune los tres pilares mas visibles de este proyecto:</p>
 * <ul>
 *   <li><b>ABSTRACCION</b>: es {@code abstract}, no se puede instanciar
 *       ("un empleado generico" no existe: siempre es asalariado, por hora,
 *       etc.). Ademas declara el metodo abstracto {@link #calcularSalario()},
 *       que obliga a cada hija a definir SU forma de calcular.</li>
 *   <li><b>ENCAPSULAMIENTO</b>: todos los atributos son {@code private} y
 *       solo se acceden por getters/setters que VALIDAN.</li>
 *   <li><b>HERENCIA</b>: las subclases heredan estos atributos y metodos
 *       comunes en lugar de repetirlos.</li>
 * </ul>
 */
public abstract class Empleado {

    // ── ENCAPSULAMIENTO: nadie fuera de la clase toca estos datos directamente ──
    private String nombre;
    private String cedula;
    private Departamento departamento;

    /**
     * Constructor protegido: solo las subclases lo invocan via {@code super(...)}.
     * Reutilizamos los setters para validar desde el momento de la creacion.
     */
    protected Empleado(String nombre, String cedula, Departamento departamento) {
        setNombre(nombre);
        setCedula(cedula);
        setDepartamento(departamento);
    }

    // ─────────────────────────────────────────────────────────────
    //  METODOS ABSTRACTOS -> cada subclase esta OBLIGADA a definirlos
    //  Aqui nace el POLIMORFISMO.
    // ─────────────────────────────────────────────────────────────

    /**
     * Calcula el salario del empleado. Cada tipo de empleado lo hace distinto.
     * @return salario total en RD$
     */
    public abstract double calcularSalario();

    /**
     * Devuelve una etiqueta con el tipo de empleado (para reportes).
     */
    public abstract String getTipo();

    // ─────────────────────────────────────────────────────────────
    //  GETTERS / SETTERS con validacion -> ENCAPSULAMIENTO
    // ─────────────────────────────────────────────────────────────

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatoInvalidoException("El nombre no puede estar vacio.");
        }
        this.nombre = nombre.trim();
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        if (cedula == null || cedula.isBlank()) {
            throw new DatoInvalidoException("La cedula no puede estar vacia.");
        }
        this.cedula = cedula.trim();
    }

    public Departamento getDepartamento() {
        return departamento;
    }

    public void setDepartamento(Departamento departamento) {
        if (departamento == null) {
            throw new DatoInvalidoException("Debe indicar un departamento.");
        }
        this.departamento = departamento;
    }

    // ─────────────────────────────────────────────────────────────
    //  POLIMORFISMO por sobrescritura de Object.toString()
    //  Se llama solo, sin importar el tipo concreto del empleado.
    // ─────────────────────────────────────────────────────────────
    @Override
    public String toString() {
        return String.format(
                "[%-11s] %-20s | Cedula: %-13s | Depto: %-16s | Salario: RD$ %,10.2f",
                getTipo(), nombre, cedula,
                departamento.getNombreLegible(), calcularSalario());
    }
}
