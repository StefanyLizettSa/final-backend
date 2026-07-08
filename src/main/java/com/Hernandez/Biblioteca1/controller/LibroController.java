package com.Hernandez.Biblioteca1.controller;

import com.Hernandez.Biblioteca1.dto.LibroDto;
import com.Hernandez.Biblioteca1.service.LibroService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/libros")
public class LibroController {
    private final LibroService libroService;

    public LibroController(LibroService libroService) {
        this.libroService = libroService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<LibroDto> libros = libroService.listarTodos();
            if (libros.isEmpty()) {
                return ResponseEntity.ok("No hay libros registrados en el sistema");
            }
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener libros: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            return libroService.buscarPorId(id)
                .map(libro -> ResponseEntity.ok((Object) libro))
                .orElse(ResponseEntity.status(404).body("Libro con ID " + id + " no encontrado"));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al buscar libro: " + e.getMessage());
        }
    }

    @GetMapping("/buscar/titulo")
    public ResponseEntity<?> buscarPorTitulo(@RequestParam String titulo) {
        try {
            List<LibroDto> libros = libroService.buscarPorTitulo(titulo);
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al buscar libros: " + e.getMessage());
        }
    }

    @GetMapping("/buscar/autor")
    public ResponseEntity<?> buscarPorAutor(@RequestParam String autor) {
        try {
            List<LibroDto> libros = libroService.buscarPorAutor(autor);
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al buscar libros: " + e.getMessage());
        }
    }

    @GetMapping("/buscar/categoria")
    public ResponseEntity<?> buscarPorCategoria(@RequestParam String categoria) {
        try {
            List<LibroDto> libros = libroService.buscarPorCategoria(categoria);
            return ResponseEntity.ok(libros);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al buscar libros: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody LibroDto dto) {
        try {
            LibroDto creado = libroService.guardar(dto);
            return ResponseEntity.status(201).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear libro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody LibroDto dto) {
        try {
            LibroDto actualizado = libroService.actualizar(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar libro: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            libroService.eliminar(id);
            return ResponseEntity.ok("Libro eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar libro: " + e.getMessage());
        }
    }
}
