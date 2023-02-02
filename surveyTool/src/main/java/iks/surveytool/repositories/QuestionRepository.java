package iks.surveytool.repositories;

import iks.surveytool.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = "SELECT * FROM question WHERE text NOT LIKE 'ENC\\_\\_##\\_\\_%'", nativeQuery = true)
    Stream<Question> findAllUnencrypted();
}
