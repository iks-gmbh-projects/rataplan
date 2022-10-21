package iks.surveytool.mapping;

import iks.surveytool.entities.Question;
import iks.surveytool.repositories.QuestionRepository;

public class MockQuestionRepository extends MockRepository.MockAbstractEntityRepository<Question> implements QuestionRepository {
    public MockQuestionRepository() {
        super(Question::new);
    }
}