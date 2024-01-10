package iks.surveytool.mapping;

import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.SurveyRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MockSurveyRepository extends MockRepository.MockAbstractEntityRepository<Survey> implements SurveyRepository {
    public MockSurveyRepository() {
        super(Survey::new);
    }

    @Override
    public Optional<Survey> findSurveyByParticipationId(String participationId) {
        return Optional.empty();
    }

    @Override
    public Optional<Survey> findSurveyByAccessId(String accessId) {
        return Optional.empty();
    }

    @Override
    public List<Survey> findAllByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(ZonedDateTime localDateTime) {
        return null;
    }

    @Override
    public List<Survey> findAllByUserId(Long userId) {
        return null;
    }

    @Override
    public long deleteSurveysByUserId(Long userId) {
        return 0;
    }
    
    @Override
    public Stream<Survey> findAllUnencrypted() {
        return findAll().stream().filter(s -> !s.getName().isEncrypted() || !s.getDescription().isEncrypted());
    }
}