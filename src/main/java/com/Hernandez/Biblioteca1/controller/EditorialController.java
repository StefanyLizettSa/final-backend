package com.Hernandez.Biblioteca1.controller;

import com.Hernandez.Biblioteca1.dto.EditorialDto;
import com.Hernandez.Biblioteca1.service.EditorialService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/api/editoriales")
public class EditorialController {
    private final EditorialService editorialService;

    public EditorialController(EditorialService editorialService) {
        this.editorialService = editorialService;
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        try {
            List<EditorialDto> editoriales = editorialService.listarTodos();
            return ResponseEntity.ok(editoriales);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al obtener editoriales: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Long id) {
        try {
            EditorialDto editorial = editorialService.buscarPorId(id);
            return ResponseEntity.ok(editorial);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al buscar editorial: " + e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(@Valid @RequestBody EditorialDto dto) {
        try {
            EditorialDto creada = editorialService.guardar(dto);
            return ResponseEntity.status(201).body(creada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear editorial: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @Valid @RequestBody EditorialDto dto) {
        try {
            EditorialDto actualizada = editorialService.actualizar(id, dto);
            return ResponseEntity.ok(actualizada);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar editorial: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            editorialService.eliminar(id);
            return ResponseEntity.ok("Editorial eliminada correctamente");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar editorial: " + e.getMessage());
        }
    }
}
