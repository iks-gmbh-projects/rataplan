package iks.surveytool.repositories;

import iks.surveytool.entities.Survey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findSurveyByParticipationId(String participationId);

    Optional<Survey> findSurveyByAccessId(String accessId);

    List<Survey> findAllByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(ZonedDateTime localDateTime);

    List<Survey> findAllByUserId(Long userId);

    long deleteSurveysByUserId(Long userId);
    
    @Query(value = "SELECT * FROM survey WHERE name NOT LIKE 'ENC\\_\\_##\\_\\_%' OR description NOT LIKE 'ENC\\_\\_##\\_\\_%'", nativeQuery = true)
    Stream<Survey> findAllUnencrypted();
}
