package iks.surveytool.mapping;

import iks.surveytool.entities.Survey;
import iks.surveytool.repositories.SurveyRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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
    public List<Survey> findSurveysByOpenAccessIsTrueAndEndDateIsAfterOrderByStartDate(ZonedDateTime localDateTime) {
        return null;
    }

    @Override
    public List<Survey> findSurveysByUserId(Long userId) {
        return null;
    }
}