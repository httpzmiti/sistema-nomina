# Sistema de Gestión de Nómina 💼

Proyecto académico (ITLA) desarrollado en **Java 21** para demostrar el dominio
de la **Programación Orientada a Objetos (POO)** y, en especial, sus **4 pilares**:
**Abstracción, Encapsulamiento, Herencia y Polimorfismo**.

Es una aplicación de consola que gestiona los empleados de una empresa y calcula
su nómina. Cada tipo de empleado calcula su salario de forma diferente.

---

## 🎯 Objetivo del proyecto

Modelar una nómina real donde conviven distintos tipos de empleados
(asalariados, por hora y freelance) y demostrar que, gracias a la POO, podemos
tratarlos a todos de forma uniforme mientras cada uno conserva su
comportamiento propio.

---

## 🧱 Los 4 pilares de la POO en este proyecto

| Pilar | Dónde se ve en el código |
|---|---|
| **Abstracción** | La clase `Empleado` es `abstract` y define el método abstracto `calcularSalario()`. La interfaz `Bonificable` define el contrato de "recibir bono". El `enum Departamento` abstrae un conjunto cerrado de valores. |
| **Encapsulamiento** | Todos los atributos son `private`. Se accede a ellos solo por *getters/setters* que **validan** los datos (nombre no vacío, salario no negativo, etc.) y lanzan `DatoInvalidoException`. La lista del `GestorNomina` se expone como solo lectura. |
| **Herencia** | `EmpleadoAsalariado`, `EmpleadoPorHora` y `Freelance` **extienden** de `Empleado`, reutilizando sus atributos y métodos comunes (`extends`). |
| **Polimorfismo** | Cada subclase **sobrescribe** `calcularSalario()` con su propia fórmula. El `GestorNomina` recorre una `List<Empleado>` y llama al mismo método sin saber el tipo concreto de cada objeto. |

---

## 📁 Estructura del proyecto

```
sistema-nomina/
├── pom.xml                     # Configuración de Maven (Java 21)
├── .gitignore
├── README.md
└── src/main/java/com/itla/nomina/
    ├── Main.java               # Menú de consola (capa de presentación)
    ├── contrato/
    │   └── Bonificable.java     # Interfaz: contrato "puede recibir bono"
    ├── modelo/
    │   ├── Empleado.java        # Clase ABSTRACTA base
    │   ├── EmpleadoAsalariado.java
    │   ├── EmpleadoPorHora.java
    │   ├── Freelance.java
    │   └── Departamento.java    # enum
    ├── servicio/
    │   └── GestorNomina.java    # Lógica de negocio (administra la nómina)
    ├── gui/
    │   └── NominaGUI.java       # Interfaz gráfica (Swing), capa de presentación alterna
    ├── excepciones/
    │   └── DatoInvalidoException.java
    └── util/
        └── ConsolaUtil.java     # Lectura y validación de entrada
```

---

## ⚙️ Requisitos previos

- **Java Development Kit (JDK) 21** o superior
- **Apache Maven 3.9+** (opcional, también se puede compilar con `javac`)

Verifica tu instalación:

```bash
java -version
mvn -version
```

---

## ▶️ Cómo compilar y ejecutar

### Opción A — Con Maven (recomendada)

```bash
# Compilar
mvn compile

# Ejecutar
mvn exec:java
```

### Opción B — Con javac (sin Maven)

```bash
# Compilar todas las clases a la carpeta target/classes
javac --release 21 -encoding UTF-8 -d target/classes $(find src/main/java -name "*.java")

# Ejecutar (consola)
java -cp target/classes com.itla.nomina.Main
```

> 💡 La bandera `--release 21` es importante si tu JDK instalado es mas nuevo
> que la 21 (por ejemplo JDK 25): sin ella, `javac` genera bytecode de tu
> version actual y el programa falla al ejecutarse con un JRE 21 (como el que
> trae por defecto la extension de Java de VS Code) con el error
> `UnsupportedClassVersionError`.

> 💡 En **Windows**, si los acentos no se ven bien en la consola, ejecuta primero
> `chcp 65001` para activar UTF-8.

### Opción C — Interfaz gráfica (Swing)

Además del menú de consola, el proyecto incluye una interfaz gráfica de
escritorio (`Swing`, incluido en el JDK, sin dependencias adicionales) que usa
exactamente la misma lógica de negocio (`GestorNomina` y las clases del
modelo).

```bash
# Con Maven
mvn compile
mvn exec:java -Dexec.mainClass=com.itla.nomina.gui.NominaGUI

# O con javac/java
javac --release 21 -encoding UTF-8 -d target/classes $(find src/main/java -name "*.java")
java -cp target/classes com.itla.nomina.gui.NominaGUI
```

La ventana permite registrar empleados (con los campos que cambian según el
tipo elegido), ver la lista en una tabla, buscar por cédula, eliminar el
empleado seleccionado, recargar los datos de ejemplo y consultar el resumen
de nómina (cantidad de empleados, total de bonos y nómina total) en tiempo
real.

### Opción D — Generar un .jar ejecutable (para correr fuera del IDE)

El proyecto produce un `.jar` de doble clic que abre directamente la interfaz
gráfica, sin necesidad de terminal ni de tener el código abierto en un editor.

```bash
# Con Maven (genera target/sistema-nomina-1.0.0.jar)
mvn clean package

# Ejecutar el jar generado
java -jar target/sistema-nomina-1.0.0.jar
```

Si no tienes Maven instalado, puedes empacarlo manualmente con las
herramientas del JDK:

```bash
javac --release 21 -encoding UTF-8 -d target/classes $(find src/main/java -name "*.java")
jar --create --file target/sistema-nomina-1.0.0.jar \
    --main-class com.itla.nomina.gui.NominaGUI -C target/classes .

java -jar target/sistema-nomina-1.0.0.jar
```

> 💡 Este `.jar` solo requiere tener **Java 21+ instalado** en la máquina
> donde se ejecute (no requiere Maven ni el código fuente). En Windows,
> normalmente basta con hacer doble clic sobre el `.jar` si hay una JRE
> asociada a esa extensión; si no abre nada, ejecútalo desde una terminal con
> `java -jar sistema-nomina-1.0.0.jar` para ver cualquier error.

---

## 🖥️ Uso

Al iniciar, el sistema carga datos de ejemplo y muestra un menú:

```
 1. Registrar empleado
 2. Listar empleados (con salario calculado)
 3. Ver nómina total y bonos
 4. Buscar empleado por cédula
 5. Recargar datos de ejemplo
 0. Salir
```

**Ejemplo de salida (listado):**

```
 1. [Asalariado ] Ana Martinez     | Cedula: 001-1111111-1 | Depto: Finanzas    | Salario: RD$  71,500.00
 2. [Por Hora   ] Carlos Reyes     | Cedula: 002-2222222-2 | Depto: Tecnologia  | Salario: RD$  80,750.00
 3. [Freelance  ] Diana Castillo   | Cedula: 003-3333333-3 | Depto: Ventas      | Salario: RD$  60,000.00
```

---

## 💰 Reglas de cálculo del salario

| Tipo | Fórmula |
|---|---|
| **Asalariado** | Salario mensual + 10% de bono |
| **Por hora** | (horas normales × tarifa) + (horas extra × tarifa × 1.5) + bono de RD$2,000 si superó 160h |
| **Freelance** | Proyectos entregados × pago por proyecto (sin bono) |

---

## 🌿 Estrategia de ramas sugerida (Git)

```bash
# Rama principal estable
main

# Rama de desarrollo de esta entrega
git checkout -b feature/init-setup
```

Flujo profesional: trabajar en ramas `feature/*`, hacer *commits* claros y
abrir un *Pull Request* hacia `main`.

---

## 🤖 Metodología de desarrollo

Este proyecto se desarrolló mediante **vibecoding**: programación asistida por
IA (Claude Code) donde el desarrollador dirige, revisa y valida cada cambio
en lugar de escribir cada línea manualmente. El diseño orientado a objetos
(qué clases existen, por qué `Empleado` es abstracta, por qué el bono va en
una interfaz separada y no en la clase padre, qué validaciones debía tener
cada setter) partió de conocimiento propio de POO y Java por parte del
estudiante; la IA se usó para acelerar la implementación, la interfaz
gráfica, el empaquetado del `.jar` y la resolución de errores puntuales
(versión de bytecode, Look&Feel de Swing, configuración de Git/GitHub).

Dicho de forma realista: es un proyecto académico funcional y correcto en
sus fundamentos de POO, construido con ayuda de IA como herramienta de
productividad, no como sustituto del entendimiento del estudiante sobre lo
que el código hace y por qué.

---

## 👤 Autor

**Luis Rainiel Pérez de los Santos** — Estudiante de Ingeniería de Software, ITLA.

## 📄 Licencia

Proyecto de uso académico.
