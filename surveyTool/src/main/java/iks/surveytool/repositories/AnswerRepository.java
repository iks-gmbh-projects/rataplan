package iks.surveytool.repositories;

import iks.surveytool.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
}