package com.Hernandez.Biblioteca1.controller;

import com.Hernandez.Biblioteca1.dto.PrestamoDto;
import com.Hernandez.Biblioteca1.service.PrestamoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prestamos")
public class PrestamoController {

    private final PrestamoService prestamoService;

    public PrestamoController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<PrestamoDto> prestamos = prestamoService.listarTodos();
            return ResponseEntity.ok(prestamos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener préstamos: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            PrestamoDto prestamo = prestamoService.buscarPorId(id);
            return ResponseEntity.ok(prestamo);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al buscar préstamo: " + e.getMessage());
        }
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        try {
            List<PrestamoDto> prestamos = prestamoService.listarPorUsuario(usuarioId);
            return ResponseEntity.ok(prestamos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener préstamos del usuario: " + e.getMessage());
        }
    }

    @GetMapping("/activos")
    public ResponseEntity<?> listarActivos() {
        try {
            List<PrestamoDto> prestamos = prestamoService.listarActivos();
            return ResponseEntity.ok(prestamos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener préstamos activos: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> registrar(@Valid @RequestBody PrestamoDto dto) {
        try {
            PrestamoDto creado = prestamoService.registrarPrestamo(dto);
            return ResponseEntity.status(201).body(creado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al registrar préstamo: " + e.getMessage());
        }
    }

    // ==========================================
    // ✅ PUT ACTUALIZAR (CON FECHAS MANUALES)
    // ==========================================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody PrestamoDto dto) {
        try {
            PrestamoDto actualizado = prestamoService.actualizarPrestamo(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar préstamo: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/devolver")
    public ResponseEntity<?> devolver(@PathVariable Long id) {
        try {
            PrestamoDto actualizado = prestamoService.devolverPrestamo(id);
            return ResponseEntity.ok(actualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al devolver préstamo: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            prestamoService.eliminar(id);
            return ResponseEntity.ok("Préstamo eliminado correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar préstamo: " + e.getMessage());
        }
    }
}