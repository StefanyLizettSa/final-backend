package com.Hernandez.Biblioteca1.repository;
import com.Hernandez.Biblioteca1.model.Editorial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface EditorialRepository extends JpaRepository<Editorial, Long> {
    Optional<Editorial> findByNombreIgnoreCase(String nombre);
}
