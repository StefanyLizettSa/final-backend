package com.Hernandez.Biblioteca1.service;
import com.Hernandez.Biblioteca1.dto.LibroDto;
import com.Hernandez.Biblioteca1.model.Categoria;
import com.Hernandez.Biblioteca1.model.Editorial;
import com.Hernandez.Biblioteca1.model.Libro;
import com.Hernandez.Biblioteca1.repository.CategoriaRepository;
import com.Hernandez.Biblioteca1.repository.EditorialRepository;
import com.Hernandez.Biblioteca1.repository.LibroRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LibroService {

    private final LibroRepository libroRepository;
    private final CategoriaRepository categoriaRepository;
    private final EditorialRepository editorialRepository;

    public LibroService(LibroRepository libroRepository,
                        CategoriaRepository categoriaRepository,
                        EditorialRepository editorialRepository) {
        this.libroRepository = libroRepository;
        this.categoriaRepository = categoriaRepository;
        this.editorialRepository = editorialRepository;
    }

    public List<LibroDto> listarTodos() {
        return libroRepository.findAll()
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public Optional<LibroDto> buscarPorId(Long id) {
        return libroRepository.findById(id)
            .map(this::convertirADto);
    }

    public List<LibroDto> buscarPorTitulo(String titulo) {
        return libroRepository.findByTituloContainingIgnoreCase(titulo)
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public List<LibroDto> buscarPorAutor(String autor) {
        return libroRepository.findByAutorContainingIgnoreCase(autor)
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public List<LibroDto> buscarPorCategoria(String categoria) {
        return libroRepository.findByCategoriaContainingIgnoreCase(categoria)
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public LibroDto guardar(LibroDto dto) {
        Libro libro = new Libro();
        libro.setTitulo(dto.getTitulo());
        libro.setAutor(dto.getAutor());
        libro.setCategoria(dto.getCategoria());
        libro.setCantidad(dto.getCantidad() != null ? dto.getCantidad() : 1);

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            libro.setCategoriaEntity(categoria);
            libro.setCategoria(categoria.getNombre());
        }

        if (dto.getEditorialId() != null) {
            Editorial editorial = editorialRepository.findById(dto.getEditorialId())
                .orElseThrow(() -> new IllegalArgumentException("Editorial no encontrada"));
            libro.setEditorial(editorial);
        }

        Libro guardado = libroRepository.save(libro);
        return convertirADto(guardado);
    }

    public LibroDto actualizar(Long id, LibroDto dto) {
        Libro libro = libroRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado con ID: " + id));

        if (dto.getTitulo() != null) libro.setTitulo(dto.getTitulo());
        if (dto.getAutor() != null) libro.setAutor(dto.getAutor());
        if (dto.getCategoria() != null) libro.setCategoria(dto.getCategoria());
        if (dto.getCantidad() != null) libro.setCantidad(dto.getCantidad());

        if (dto.getCategoriaId() != null) {
            Categoria categoria = categoriaRepository.findById(dto.getCategoriaId())
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            libro.setCategoriaEntity(categoria);
            libro.setCategoria(categoria.getNombre());
        }

        if (dto.getEditorialId() != null) {
            Editorial editorial = editorialRepository.findById(dto.getEditorialId())
                .orElseThrow(() -> new IllegalArgumentException("Editorial no encontrada"));
            libro.setEditorial(editorial);
        }

        Libro actualizado = libroRepository.save(libro);
        return convertirADto(actualizado);
    }

    public void eliminar(Long id) {
        if (!libroRepository.existsById(id)) {
            throw new IllegalArgumentException("Libro no encontrado con ID: " + id);
        }
        libroRepository.deleteById(id);
    }

    // ✅ MÉTODO PARA REDUCIR CANTIDAD (cuando se presta)
    public synchronized void reducirCantidad(Long libroId) {
        Libro libro = libroRepository.findById(libroId)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        if (libro.getCantidad() <= 0) {
            throw new IllegalArgumentException("No hay copias disponibles de: " + libro.getTitulo());
        }
        
        libro.setCantidad(libro.getCantidad() - 1);
        libroRepository.save(libro);
    }

    // ✅ MÉTODO PARA AUMENTAR CANTIDAD (cuando se devuelve)
    public synchronized void aumentarCantidad(Long libroId) {
        Libro libro = libroRepository.findById(libroId)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        libro.setCantidad(libro.getCantidad() + 1);
        libroRepository.save(libro);
    }

    private LibroDto convertirADto(Libro libro) {
        LibroDto dto = new LibroDto(
            libro.getId(),
            libro.getTitulo(),
            libro.getAutor(),
            libro.getCategoria(),
            libro.getCantidad()
        );

        if (libro.getCategoriaEntity() != null) {
            dto.setCategoriaId(libro.getCategoriaEntity().getId());
            dto.setCategoriaNombre(libro.getCategoriaEntity().getNombre());
        }

        if (libro.getEditorial() != null) {
            dto.setEditorialId(libro.getEditorial().getId());
            dto.setEditorialNombre(libro.getEditorial().getNombre());
        }

        return dto;
    }
}