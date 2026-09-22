package com.itla.nomina.util;

import java.util.Scanner;

/**
 * Utilidades para leer datos por consola de forma segura.
 *
 * <p>Centraliza la lectura y validacion de entrada para no repetir codigo
 * en el menu (principio DRY: Don't Repeat Yourself). Si el usuario escribe
 * algo invalido, se le vuelve a preguntar en lugar de romper el programa.</p>
 */
public final class ConsolaUtil {

    private final Scanner scanner;

    public ConsolaUtil(Scanner scanner) {
        this.scanner = scanner;
    }

    /** Lee una linea de texto no vacia. */
    public String leerTexto(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            String entrada = scanner.nextLine().trim();
            if (!entrada.isEmpty()) {
                return entrada;
            }
            System.out.println("  [!] El valor no puede estar vacio. Intente de nuevo.");
        }
    }

    /** Lee un entero dentro de un rango [min, max]. */
    public int leerEnteroEnRango(String etiqueta, int min, int max) {
        while (true) {
            System.out.print(etiqueta);
            String entrada = scanner.nextLine().trim();
            try {
                int valor = Integer.parseInt(entrada);
                if (valor >= min && valor <= max) {
                    return valor;
                }
                System.out.printf("  [!] Debe estar entre %d y %d.%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("  [!] Debe escribir un numero entero valido.");
            }
        }
    }

    /** Lee un numero decimal mayor o igual a cero. */
    public double leerDecimalPositivo(String etiqueta) {
        while (true) {
            System.out.print(etiqueta);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            try {
                double valor = Double.parseDouble(entrada);
                if (valor >= 0) {
                    return valor;
                }
                System.out.println("  [!] El numero no puede ser negativo.");
            } catch (NumberFormatException e) {
                System.out.println("  [!] Debe escribir un numero valido.");
            }
        }
    }
}
