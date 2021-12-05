package madstodolist.controller;

public class TareaData {
    private String titulo;
    private String descripcion;
    private String estado;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() { return estado; }

    public void setEstado(String estado) { this.estado = estado; }
}
