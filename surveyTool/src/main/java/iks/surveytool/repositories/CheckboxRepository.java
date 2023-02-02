package iks.surveytool.repositories;

import iks.surveytool.entities.Checkbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface CheckboxRepository extends JpaRepository<Checkbox, Long> {
    @Query(value = "SELECT * FROM checkbox WHERE text NOT LIKE 'ENC\\_\\_##\\_\\_%'", nativeQuery = true)
    Stream<Checkbox> findAllUnencrypted();
}