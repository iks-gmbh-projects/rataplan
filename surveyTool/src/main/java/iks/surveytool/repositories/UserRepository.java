package iks.surveytool.repositories;

import iks.surveytool.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "SELECT DISTINCT u " +
            "FROM User u, Answer a, Question q, QuestionGroup qg, Survey s " +
            "WHERE u.id = a.user " +
            "AND a.question = q.id " +
            "AND q.questionGroup = qg.id " +
            "AND qg.survey = s.id " +
            "AND s.id = :surveyID")
    List<User> findParticipatingUsersBySurveyId(@Param("surveyID") Long surveyID);
}