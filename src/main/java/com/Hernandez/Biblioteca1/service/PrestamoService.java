package com.Hernandez.Biblioteca1.service;
import com.Hernandez.Biblioteca1.dto.PrestamoDto;
import com.Hernandez.Biblioteca1.model.Libro;
import com.Hernandez.Biblioteca1.model.Prestamo;
import com.Hernandez.Biblioteca1.model.Usuario;
import com.Hernandez.Biblioteca1.repository.LibroRepository;
import com.Hernandez.Biblioteca1.repository.PrestamoRepository;
import com.Hernandez.Biblioteca1.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final UsuarioRepository usuarioRepository;
    private final LibroRepository libroRepository;

    // ✅ PLAZO MÁXIMO DE PRÉSTAMO (días)
    private static final int PLAZO_MAXIMO = 7;

    public PrestamoService(PrestamoRepository prestamoRepository,
                           UsuarioRepository usuarioRepository,
                           LibroRepository libroRepository) {
        this.prestamoRepository = prestamoRepository;
        this.usuarioRepository = usuarioRepository;
        this.libroRepository = libroRepository;
    }

    public List<PrestamoDto> listarTodos() {
        return prestamoRepository.findAll()
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public PrestamoDto buscarPorId(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado con ID: " + id));
        return convertirADto(prestamo);
    }

    public List<PrestamoDto> listarPorUsuario(Long usuarioId) {
        return prestamoRepository.findByUsuarioId(usuarioId)
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    public List<PrestamoDto> listarActivos() {
        return prestamoRepository.findByEstado("ACTIVO")
            .stream()
            .map(this::convertirADto)
            .collect(Collectors.toList());
    }

    // ==========================================
    // REGISTRAR PRÉSTAMO - FECHA DEVOLUCIÓN AUTOMÁTICA
    // ==========================================
    public PrestamoDto registrarPrestamo(PrestamoDto dto) {
        Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Libro libro = libroRepository.findById(dto.getLibroId())
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        if (libro.getCantidad() <= 0) {
            throw new IllegalArgumentException("No hay copias disponibles de: " + libro.getTitulo());
        }

        Prestamo prestamo = new Prestamo();
        
        // ✅ FECHA DE PRÉSTAMO
        LocalDate fechaPrestamo = LocalDate.now();
        prestamo.setFechaPrestamo(fechaPrestamo);
        
        // ✅ FECHA DE DEVOLUCIÓN AUTOMÁTICA = fechaPrestamo + 7 días
        LocalDate fechaDevolucion = fechaPrestamo.plusDays(PLAZO_MAXIMO);
        prestamo.setFechaDevolucion(fechaDevolucion);
        
        prestamo.setEstado("ACTIVO");
        prestamo.setUsuario(usuario);
        prestamo.setLibro(libro);

        // ✅ MENSAJE: Fecha de devolución estimada
        prestamo.setMensaje("📖 PRÉSTAMO REGISTRADO: Devolver antes del " + fechaDevolucion.toString() + " (plazo: " + PLAZO_MAXIMO + " días)");

        // ✅ REDUCIR CANTIDAD EN 1
        libro.setCantidad(libro.getCantidad() - 1);
        libroRepository.save(libro);

        Prestamo guardado = prestamoRepository.save(prestamo);
        return convertirADto(guardado);
    }

    // ==========================================
    // DEVOLVER PRÉSTAMO - CALCULA RETRASO AUTOMÁTICO
    // ==========================================
    public PrestamoDto devolverPrestamo(Long id) {
        Prestamo prestamo = prestamoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

        if ("DEVUELTO".equals(prestamo.getEstado())) {
            throw new IllegalArgumentException("El préstamo ya fue devuelto");
        }

        LocalDate hoy = LocalDate.now();
        LocalDate fechaPrestamo = prestamo.getFechaPrestamo();
        LocalDate fechaDevolucionEstimada = prestamo.getFechaDevolucion();

        prestamo.setEstado("DEVUELTO");
        
        // ✅ CALCULAR DIAS TRANSCURRIDOS Y RETRASO
        long diasTranscurridos = ChronoUnit.DAYS.between(fechaPrestamo, hoy);
        
        String mensaje;
        if (diasTranscurridos > PLAZO_MAXIMO) {
            long diasRetraso = diasTranscurridos - PLAZO_MAXIMO;
            mensaje = "📕 RETRASADO: El libro fue devuelto con " + diasRetraso + " día(s) de retraso (plazo: " + PLAZO_MAXIMO + " días)";
        } else {
            mensaje = "✅ ENTREGADO A TIEMPO: El libro fue devuelto en " + diasTranscurridos + " día(s) (plazo: " + PLAZO_MAXIMO + " días)";
        }
        prestamo.setMensaje(mensaje);

        // ✅ AUMENTAR CANTIDAD EN 1
        Libro libro = prestamo.getLibro();
        libro.setCantidad(libro.getCantidad() + 1);
        libroRepository.save(libro);

        Prestamo actualizado = prestamoRepository.save(prestamo);
        return convertirADto(actualizado);
    }

    // ==========================================
    // ACTUALIZAR PRÉSTAMO (PUT) - RECALCULAR MENSAJE
    // ==========================================
    public PrestamoDto actualizarPrestamo(Long id, PrestamoDto dto) {
        Prestamo prestamo = prestamoRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado con ID: " + id));

        // Solo permite actualizar usuario o libro, no fechas
        if (dto.getUsuarioId() != null) {
            Usuario usuario = usuarioRepository.findById(dto.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            prestamo.setUsuario(usuario);
        }

        if (dto.getLibroId() != null) {
            Libro libro = libroRepository.findById(dto.getLibroId())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            prestamo.setLibro(libro);
        }

        // ✅ RECALCULAR MENSAJE SEGÚN ESTADO
        if ("ACTIVO".equals(prestamo.getEstado())) {
            LocalDate hoy = LocalDate.now();
            LocalDate fechaDevolucion = prestamo.getFechaDevolucion();
            long diasRestantes = ChronoUnit.DAYS.between(hoy, fechaDevolucion);
            
            if (diasRestantes < 0) {
                long diasRetraso = Math.abs(diasRestantes);
                prestamo.setMensaje("⚠️ RETRASADO: El libro debe devolverse hace " + diasRetraso + " día(s)");
            } else if (diasRestantes == 0) {
                prestamo.setMensaje("⚠️ ÚLTIMO DÍA: El libro debe devolverse HOY");
            } else if (diasRestantes <= 2) {
                prestamo.setMensaje("⏰ PRÓXIMO VENCIMIENTO: Quedan " + diasRestantes + " día(s) para devolver");
            } else {
                prestamo.setMensaje("📖 EN PRÉSTAMO: Quedan " + diasRestantes + " día(s) para devolver (plazo: " + PLAZO_MAXIMO + " días)");
            }
        }

        Prestamo actualizado = prestamoRepository.save(prestamo);
        return convertirADto(actualizado);
    }

    public void eliminar(Long id) {
        if (!prestamoRepository.existsById(id)) {
            throw new IllegalArgumentException("Préstamo no encontrado con ID: " + id);
        }
        prestamoRepository.deleteById(id);
    }

    private PrestamoDto convertirADto(Prestamo prestamo) {
        PrestamoDto dto = new PrestamoDto();
        dto.setId(prestamo.getId());
        dto.setFechaPrestamo(prestamo.getFechaPrestamo());
        dto.setFechaDevolucion(prestamo.getFechaDevolucion());
        dto.setEstado(prestamo.getEstado());
        dto.setMensaje(prestamo.getMensaje());
        dto.setUsuarioId(prestamo.getUsuario().getId());
        dto.setUsuarioNombre(prestamo.getUsuario().getNombre());
        dto.setLibroId(prestamo.getLibro().getId());
        dto.setLibroTitulo(prestamo.getLibro().getTitulo());
        return dto;
    }
}