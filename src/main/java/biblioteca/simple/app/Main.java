package biblioteca.simple.app;

import biblioteca.simple.contratos.Prestable;
import biblioteca.simple.modelo.*;
import biblioteca.simple.servicios.Catalogo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Main {


    private static final Catalogo catalogo = new Catalogo();
    private static final List<Usuario> usuarios =new ArrayList<>();
    private static final Scanner sc = new Scanner(System.in);
    private static final String ARCHIVO_USUARIOS = "usuarios.json";



    public static void main(String[] args) {
        cargarDatos();
        cargarUsuariosJSON();
        menu();
    }

    private static void cargarDatos(){
        catalogo.alta(new Libro(1, "El Quijote", "1608", Formato.FISICO, "25225", "Cervantes"));
        catalogo.alta(new Libro(2, "El nombre del viento", "2007", Formato.FISICO, "9788401352836", "Patrick Rothfuss"));
        catalogo.alta(new Pelicula(3, "El Padrino", "1972", Formato.FISICO, "rancis Ford Coppola", 175));
        catalogo.alta(new Pelicula(4, "Parásitos", "2019", Formato.FISICO, "Bong Joon-ho", 132));
        catalogo.alta(new Videojuego(5, "Elden Ring", "2022", Formato.DIGITAL, "PC/PS5/Xbox", "RPG"));
        catalogo.alta(new Videojuego(6, "Zelda Breath of the wild", "2017", Formato.FISICO, "Nintendo Switch", "Aventura"));


        if (new File(ARCHIVO_USUARIOS).exists()) {
            usuarios.add(new Usuario(1, "Juan"));
            usuarios.add(new Usuario(2, "María"));
        }
    }

    private static void menu(){

        int op;

        do {

            System.out.println("\n===Menú de Biblioteca===");
            System.out.println("1. Listar");
            System.out.println("2. Buscar por título");
            System.out.println("3. Buscar por año");
            System.out.println("4. Prestar Producto");
            System.out.println("5. Devolver Producto");
            System.out.println("6. Crear Usuarios");
            System.out.println("7. Listar Usuarios");
            System.out.println("8. Exportar usuarios a JSON");
            System.out.println("9. Importar Uusuarios desde JSON");
            System.out.println("0. Salir");
            System.out.println("\nSelecciona una opción: ");

            while(!sc.hasNextInt()) sc.next();
            op = sc.nextInt();

            sc.nextLine();

            switch (op){
                case 1 -> listar();
                case 2 -> buscarPorTitulo();
                case 3 -> buscarPorAnio();
                case 4 -> prestar();
                case 5 -> devolver();
                case 6 -> crearUsuario();
                case 7 -> listarUsuarios();
                case 8 -> exportarUsuariosJSON();
                case 9 -> cargarUsuariosJSON();
                case 0 -> System.out.println("Sayonara!");
                default -> System.out.println("Opción no válida");
            }

        } while(op != 0);
    }

    private static void listar(){
        List<Producto> lista = catalogo.listar();

        if(lista.isEmpty()){
            System.out.println("Catálogo vacío");
            return;
        }

        System.out.println("==Lista de productos ===");

        for(Producto p : lista) System.out.println("- " + p);


    }

    private static void buscarPorTitulo(){
        System.out.println("Título (escribe parte del título): ");
        String t = sc.nextLine();
        catalogo.buscar(t).forEach(p -> System.out.println("- " + p));
    }

    private static void buscarPorAnio(){
        System.out.println("Año: ");
        int a = sc.nextInt();
        sc.nextLine();
        catalogo.buscar(a).forEach(p -> System.out.println("- " + p));
    }

    private static void listarUsuarios(){
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados");
            return;
        }
        System.out.println("Lista usuarios");
        usuarios.forEach( u ->
                        System.out.println("- Código : " + u.getId() + "| Nombre: " + u.getNombre() )
        );
    }

    private static Usuario getUsuarioPorCodigo(int id){
        return usuarios.stream()
                .filter(u -> u.getId() == id)
                .findFirst()
                .orElse(null);
    }

    private static void crearUsuario() {
        System.out.print("Ingresa el código del nuevo usuario: ");
        while (!sc.hasNextInt()) {
            sc.next();
            System.out.print("Por favor, ingresa un código válido: ");
        }
        int id = sc.nextInt();
        sc.nextLine();

        // Verifica si el ID ya existe
        if (getUsuarioPorCodigo(id) != null) {
            System.out.println("Error: Ya existe un usuario con ese código");
            return;
        }

        System.out.print("Ingresa el nombre del usuario: ");
        String nombre = sc.nextLine();

        usuarios.add(new Usuario(id, nombre));
        System.out.println("✓ Usuario creado correctamente: " + nombre);
    }


    //Creación de usuario en mitad del programa.
    private static Usuario crearUsuarioEnPrestamo(int id) {
        System.out.println("\n>>> El usuario no existe. ¿Deseas crearlo ahora? (s/n)");
        String respuesta = sc.nextLine().toLowerCase();

        if (!respuesta.equals("s")) {
            return null;
        }

        System.out.print("Ingresa el nombre del nuevo usuario: ");
        String nombre = sc.nextLine();

        Usuario nuevoUsuario = new Usuario(id, nombre);
        usuarios.add(nuevoUsuario);
        System.out.println("✓ Usuario creado correctamente: " + nombre);

        return nuevoUsuario;
    }



    private static void prestar(){

        // 1)mostrar productos disponibles

        List<Producto> disponibles = catalogo.listar().stream()
                .filter(p -> p instanceof Prestable pN && !pN.estaPrestado())
                .collect(Collectors.toList());

        if ( disponibles.isEmpty() ) {
            System.out.println("No hay productos para prestar");
            return;
        }

        System.out.println("-- PRODUCTOS DISPONIBLES --");
        disponibles.forEach( p -> System.out.println("- ID: " + p.getId() + " | " + p));

        System.out.println("Escribe el id del producto: ");
        int id = sc.nextInt();
        sc.nextLine();

        Producto pEncontrado = disponibles.stream()
                .filter(p -> {
                    try {
                        return p.getId() == id;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })

                .findFirst()
                .orElse(null);

                 if (pEncontrado == null){
                     System.out.println("El id no existe");
                     return;
                 }


                 listarUsuarios();

                 System.out.println("Ingresa código de usurio");

                 int cUsuario = sc.nextInt();
                 sc.nextLine();
                 Usuario u1 = getUsuarioPorCodigo(cUsuario);

                 if (u1 == null) {
                     // Ofrecer crear el usuario
                     u1 = crearUsuarioEnPrestamo(cUsuario);
                     if (u1 == null) {
                         System.out.println("Operación cancelada");
                         return;
                     }
                    }

                 Prestable pPrestable = (Prestable) pEncontrado;
                 pPrestable.prestar(u1);

    }


    public static void devolver(){


        List<Producto> pPrestados = catalogo.listar().stream()
                .filter(p -> p instanceof Prestable pN && pN.estaPrestado())
                .collect(Collectors.toList());

        if ( pPrestados.isEmpty() ) {
            System.out.println("No hay productos para prestar");
            return;
        }

        System.out.println("-- PRODUCTOS PRESTADOS --");
        pPrestados.forEach( p -> System.out.println("- ID: " + p.getId() + " | " + p));

        System.out.println("Escribe el id del producto: ");
        int id = sc.nextInt();
        sc.nextLine();

        Producto pEncontrado = pPrestados.stream()
                .filter(p -> {
                    try {
                        return p.getId() == id;
                    } catch (NumberFormatException e) {
                        return false;
                    }
                })

                .findFirst()
                .orElse(null);

        if (pEncontrado == null){
            System.out.println("El id no existe");
            return;
        }

        Prestable pE = (Prestable) pEncontrado;
        pE.devolver();
        System.out.println("Devuelto correctamente");

    }

// JSON

private static void exportarUsuariosJSON() {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_USUARIOS))) {
        writer.write("[\n");

        for (int i = 0; i < usuarios.size(); i++) {
            Usuario u = usuarios.get(i);
            writer.write("  {\n");
            writer.write("    \"id\": " + u.getId() + ",\n");
            writer.write("    \"nombre\": \"" + escaparJSON(u.getNombre()) + "\"\n");
            writer.write("  }");

            if (i < usuarios.size() - 1) {
                writer.write(",");
            }
            writer.write("\n");
        }

        writer.write("]\n");
        System.out.println("✓ Usuarios exportados correctamente a " + ARCHIVO_USUARIOS);

    } catch (IOException e) {
        System.out.println("Error al exportar usuarios: " + e.getMessage());
    }
}

    private static void cargarUsuariosJSON() {
        File archivo = new File(ARCHIVO_USUARIOS);

        if (!archivo.exists()) {
            System.out.println("No se encontró el archivo " + ARCHIVO_USUARIOS);
            return;
        }

        try {
            String contenido = new String(Files.readAllBytes(Paths.get(ARCHIVO_USUARIOS)));
            usuarios.clear();

            // Parser simple de JSON
            contenido = contenido.trim();
            if (!contenido.startsWith("[") || !contenido.endsWith("]")) {
                throw new IOException("Formato JSON inválido");
            }

            // Extraer objetos entre llaves
            String[] objetos = contenido.substring(1, contenido.length() - 1).split("\\},\\s*\\{");

            for (String obj : objetos) {
                obj = obj.replaceAll("[\\[\\]\\{\\}]", "").trim();
                if (obj.isEmpty()) continue;

                int id = -1;
                String nombre = "";

                String[] propiedades = obj.split(",");
                for (String prop : propiedades) {
                    String[] partes = prop.split(":");
                    if (partes.length != 2) continue;

                    String clave = partes[0].trim().replaceAll("\"", "");
                    String valor = partes[1].trim().replaceAll("\"", "");

                    if (clave.equals("id")) {
                        id = Integer.parseInt(valor);
                    } else if (clave.equals("nombre")) {
                        nombre = valor;
                    }
                }

                if (id != -1 && !nombre.isEmpty()) {
                    usuarios.add(new Usuario(id, nombre));
                }
            }

            System.out.println("✓ Se cargaron " + usuarios.size() + " usuarios desde " + ARCHIVO_USUARIOS);

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error al importar usuarios: " + e.getMessage());
        }
    }

    // Método auxiliar para escapar caracteres especiales en JSON
    private static String escaparJSON(String texto) {
        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

}
