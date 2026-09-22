package com.itla.nomina.excepciones;

/**
 * Excepcion personalizada que se lanza cuando un dato de un empleado
 * no cumple las reglas de negocio (nombre vacio, salario negativo, etc.).
 *
 * <p>La usamos para reforzar el ENCAPSULAMIENTO: los setters validan y,
 * si el dato es invalido, lanzan esta excepcion en lugar de guardar basura.</p>
 *
 * <p>Extiende de RuntimeException (excepcion no verificada) porque un dato
 * invalido es un error de programacion/uso, no una condicion recuperable
 * que el llamador deba manejar obligatoriamente.</p>
 */
public class DatoInvalidoException extends RuntimeException {

    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
