package iks.surveytool.mapping;

import iks.surveytool.entities.Question;
import iks.surveytool.repositories.QuestionRepository;

import java.util.stream.Stream;

public class MockQuestionRepository extends MockRepository.MockAbstractEntityRepository<Question> implements QuestionRepository {
    public MockQuestionRepository() {
        super(Question::new);
    }
    
    @Override
    public Stream<Question> findAllUnencrypted() {
        return findAll().stream().filter(q -> !q.getText().isEncrypted());
    }
}