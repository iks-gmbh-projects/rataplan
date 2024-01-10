package iks.surveytool.repositories;

import iks.surveytool.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    @Query(value = "SELECT * FROM answer WHERE text IS NOT NULL AND text NOT LIKE 'ENC\\_\\_##\\_\\_%'", nativeQuery = true)
    Stream<Answer> findAllUnencrypted();
}
