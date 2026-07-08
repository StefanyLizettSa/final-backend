package com.Hernandez.Biblioteca1.service;
import com.Hernandez.Biblioteca1.dto.CategoriaDto;
import com.Hernandez.Biblioteca1.model.Categoria;
import com.Hernandez.Biblioteca1.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class CategoriaService {
    private final CategoriaRepository categoriaRepository;

    public CategoriaService(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    public List<CategoriaDto> listarTodos() {
        return categoriaRepository.findAll()
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public CategoriaDto buscarPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        return convertirADto(categoria);
    }

    public CategoriaDto guardar(CategoriaDto dto) {
        if (categoriaRepository.findByNombreIgnoreCase(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + dto.getNombre());
        }

        Categoria categoria = new Categoria(dto.getNombre(), dto.getDescripcion());
        Categoria guardada = categoriaRepository.save(categoria);
        return convertirADto(guardada);
    }

    public CategoriaDto actualizar(Long id, CategoriaDto dto) {
        Categoria categoria = categoriaRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));

        if (dto.getNombre() != null && !dto.getNombre().equals(categoria.getNombre())) {
            if (categoriaRepository.findByNombreIgnoreCase(dto.getNombre()).isPresent()) {
                throw new IllegalArgumentException("Ya existe una categoría con el nombre: " + dto.getNombre());
            }
            categoria.setNombre(dto.getNombre());
        }

        if (dto.getDescripcion() != null) {
            categoria.setDescripcion(dto.getDescripcion());
        }

        Categoria actualizada = categoriaRepository.save(categoria);
        return convertirADto(actualizada);
    }

    public void eliminar(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
    }

    private CategoriaDto convertirADto(Categoria categoria) {
        return new CategoriaDto(categoria.getId(), categoria.getNombre(), categoria.getDescripcion());
    }
}
