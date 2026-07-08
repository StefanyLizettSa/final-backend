package com.Hernandez.Biblioteca1.dto;
import jakarta.validation.constraints.NotBlank;
public class EditorialDto {
    private Long id;

    @NotBlank(message = "El nombre de la editorial es obligatorio")
    private String nombre;

    private String pais;

    public EditorialDto() {}

    public EditorialDto(Long id, String nombre, String pais) {
        this.id = id;
        this.nombre = nombre;
        this.pais = pais;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
}
