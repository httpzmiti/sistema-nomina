package com.itla.nomina.modelo;

/**
 * Departamentos de la empresa.
 *
 * <p>Un {@code enum} es una forma de ABSTRACCION: en lugar de usar textos
 * sueltos como "ventas", "Ventas" o "VENTA" (propensos a errores de tipeo),
 * modelamos un conjunto CERRADO y seguro de valores posibles.</p>
 *
 * <p>Cada constante guarda ademas un nombre legible para mostrar al usuario.</p>
 */
public enum Departamento {

    TECNOLOGIA("Tecnologia"),
    RECURSOS_HUMANOS("Recursos Humanos"),
    VENTAS("Ventas"),
    FINANZAS("Finanzas"),
    OPERACIONES("Operaciones");

    // Atributo privado -> encapsulamiento tambien aplica a los enums
    private final String nombreLegible;

    Departamento(String nombreLegible) {
        this.nombreLegible = nombreLegible;
    }

    public String getNombreLegible() {
        return nombreLegible;
    }
}
