package com.Hernandez.Biblioteca1.repository;

import com.Hernandez.Biblioteca1.model.Libro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibroRepository extends JpaRepository<Libro, Long> {

    List<Libro> findByTituloContainingIgnoreCase(String titulo);

    List<Libro> findByAutorContainingIgnoreCase(String autor);

    List<Libro> findByCategoriaContainingIgnoreCase(String categoria);

    // ✅ NUEVO: Buscar libros con cantidad > 0 (disponibles)
    List<Libro> findByCantidadGreaterThan(Integer cantidad);

    // ✅ NUEVO: Buscar libros con cantidad = 0 (agotados)
    List<Libro> findByCantidadEquals(Integer cantidad);
}
