package com.Hernandez.Biblioteca1.dto;
public class LoginResponse {

    private String tipo;
    private String email;
    private String nombre;
    private String rol;

    public LoginResponse() {}

    public LoginResponse(String email, String nombre, String rol) {
        this.tipo = "Basic";
        this.email = email;
        this.nombre = nombre;
        this.rol = rol;
    }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
