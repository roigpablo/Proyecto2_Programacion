package biblioteca.simple.modelo;

import biblioteca.simple.contratos.Prestable;

public class Videojuego extends Producto implements Prestable {

    private String plataforma;
    private String genero;

    private boolean prestado;
    private Usuario prestadoA;

    public Videojuego(int id, String titulo, String anho, Formato formato, String plataforma, String genero) {
        super(id, titulo, anho, formato);
        this.plataforma = plataforma;
        this.genero = genero;
    }

    public Videojuego(String titulo, String anho, Formato formato, String plataforma, String genero) {
        super(titulo, anho, formato);
        this.plataforma = plataforma;
        this.genero = genero;
    }


    public String getPlataforma() {
        return plataforma;
    }

    public String getGenero() {
        return genero;
    }

    //Metodo prestable
    @Override
    public void prestar(Usuario u) {
        if (prestado) throw new IllegalStateException("Ya presado!");

        this.prestadoA = u;
    }


    // Devuelve libro. MArca como no prestado
    @Override
    public void devolver() {
        this.prestado = false;
        this.prestadoA = null;
    }

    // Comprueba si está prestado
    @Override
    public boolean estaPrestado() {
        return this.prestado;
    }

    @Override
    public String toString() {
        return "Videojuego{" +
                "Plataforma='" + plataforma + '\'' +
                ", genero='" + genero + '\'' +
                ", id=" + id +
                ", titulo='" + titulo + '\'' +
                ", anho='" + anho + '\'' +
                ", formato=" + formato +
                '}';
    }







}


