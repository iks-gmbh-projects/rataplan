package iks.surveytool.repositories;

import iks.surveytool.entities.QuestionGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.stream.Stream;

public interface QuestionGroupRepository extends JpaRepository<QuestionGroup, Long> {
    @Query(value = "SELECT * FROM questionGroup WHERE title NOT LIKE 'ENC\\_\\_##\\_\\_%'", nativeQuery = true)
    Stream<QuestionGroup> findAllUnencrypted();
}
