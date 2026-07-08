package com.Hernandez.Biblioteca1.service;
import com.Hernandez.Biblioteca1.dto.EditorialDto;
import com.Hernandez.Biblioteca1.model.Editorial;
import com.Hernandez.Biblioteca1.repository.EditorialRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EditorialService {
    private final EditorialRepository editorialRepository;

    public EditorialService(EditorialRepository editorialRepository) {
        this.editorialRepository = editorialRepository;
    }

    public List<EditorialDto> listarTodos() {
        return editorialRepository.findAll()
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public EditorialDto buscarPorId(Long id) {
        Editorial editorial = editorialRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Editorial no encontrada"));
        return convertirADto(editorial);
    }

    public EditorialDto guardar(EditorialDto dto) {
        if (editorialRepository.findByNombreIgnoreCase(dto.getNombre()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una editorial con el nombre: " + dto.getNombre());
        }

        Editorial editorial = new Editorial(dto.getNombre(), dto.getPais());
        Editorial guardada = editorialRepository.save(editorial);
        return convertirADto(guardada);
    }

    public EditorialDto actualizar(Long id, EditorialDto dto) {
        Editorial editorial = editorialRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Editorial no encontrada"));

        if (dto.getNombre() != null && !dto.getNombre().equals(editorial.getNombre())) {
            if (editorialRepository.findByNombreIgnoreCase(dto.getNombre()).isPresent()) {
                throw new IllegalArgumentException("Ya existe una editorial con el nombre: " + dto.getNombre());
            }
            editorial.setNombre(dto.getNombre());
        }

        if (dto.getPais() != null) {
            editorial.setPais(dto.getPais());
        }

        Editorial actualizada = editorialRepository.save(editorial);
        return convertirADto(actualizada);
    }

    public void eliminar(Long id) {
        if (!editorialRepository.existsById(id)) {
            throw new IllegalArgumentException("Editorial no encontrada");
        }
        editorialRepository.deleteById(id);
    }

    private EditorialDto convertirADto(Editorial editorial) {
        return new EditorialDto(editorial.getId(), editorial.getNombre(), editorial.getPais());
    }
}
