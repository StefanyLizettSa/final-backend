package com.Hernandez.Biblioteca1.service;
import com.Hernandez.Biblioteca1.dto.UsuarioDto;
import com.Hernandez.Biblioteca1.model.Usuario;
import com.Hernandez.Biblioteca1.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }
    public List<UsuarioDto> listarTodos() {
        return usuarioRepository.findAll()
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public List<UsuarioDto> listarPorRol(String rol) {
        return usuarioRepository.findAll()
            .stream()
            .filter(usuario -> usuario.getRol().equalsIgnoreCase(rol))
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public UsuarioDto buscarPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con ID: " + id));
        return convertirADto(usuario);
    }

    private UsuarioDto convertirADto(Usuario usuario) {
        UsuarioDto dto = new UsuarioDto();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setEmail(usuario.getEmail());
        dto.setRol(usuario.getRol());
        return dto;
    }
}