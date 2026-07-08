package com.Hernandez.Biblioteca1.controller;
import com.Hernandez.Biblioteca1.dto.UsuarioDto;
import com.Hernandez.Biblioteca1.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<?> listarTodos() {
        try {
            List<UsuarioDto> usuarios = usuarioService.listarTodos();
            if (usuarios.isEmpty()) {
                return ResponseEntity.ok("No hay usuarios registrados en el sistema");
            }
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener usuarios: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            UsuarioDto usuario = usuarioService.buscarPorId(id);
            return ResponseEntity.ok(usuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al buscar usuario: " + e.getMessage());
        }
    }

    @GetMapping("/rol/{rol}")
    public ResponseEntity<?> listarPorRol(@PathVariable String rol) {
        try {
            List<UsuarioDto> usuarios = usuarioService.listarPorRol(rol);
            if (usuarios.isEmpty()) {
                return ResponseEntity.ok("No hay usuarios con el rol: " + rol);
            }
            return ResponseEntity.ok(usuarios);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener usuarios por rol: " + e.getMessage());
        }
    }
}