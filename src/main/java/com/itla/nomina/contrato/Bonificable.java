package com.itla.nomina.contrato;

/**
 * Contrato para todo empleado que puede recibir un BONO.
 *
 * <p>Esto es ABSTRACCION mediante una interfaz: define el "que" (existe un
 * bono que se puede calcular) sin decir el "como". Cada clase que la implemente
 * decidira su propia formula.</p>
 *
 * <p>Punto clave para el video: NO todos los empleados son bonificables.
 * Por eso el bono es una INTERFAZ separada de la herencia, y no un metodo
 * dentro de la clase padre Empleado. Asi separamos:
 * <ul>
 *   <li>Herencia (extends)  -> "ES UN" empleado.</li>
 *   <li>Interfaz (implements) -> "PUEDE HACER" algo (recibir bono).</li>
 * </ul>
 * </p>
 */
public interface Bonificable {

    /**
     * Calcula el monto del bono segun la regla de cada clase.
     * @return monto del bono en RD$
     */
    double calcularBono();

    /**
     * Metodo DEFAULT (Java 8+): comportamiento comun que se hereda gratis
     * al implementar la interfaz. Demuestra que una interfaz tambien puede
     * aportar codigo, no solo firmas.
     */
    default String descripcionBono() {
        return String.format("Bono aplicado: RD$ %.2f", calcularBono());
    }
}
