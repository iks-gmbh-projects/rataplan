package iks.surveytool.runners;

import iks.surveytool.entities.*;
import iks.surveytool.repositories.SurveyRepository;
import iks.surveytool.services.CryptoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Transactional
public class DBEncrypter implements ApplicationRunner {
    private final SurveyRepository surveyRepository;
    private final CryptoService cryptoService;
    @Override
    public void run(ApplicationArguments args) {
        for(Survey survey:surveyRepository.findAll()) {
            boolean altered = ensureEncrypted(
                survey::getName,
                survey::setName
            );
            altered = altered || ensureEncrypted(
                survey::getDescription,
                survey::setDescription
            );
            for(QuestionGroup questionGroup: survey.getQuestionGroups()) {
                altered = altered || ensureEncrypted(
                    questionGroup::getTitle,
                    questionGroup::setTitle
                );
                for(Question question: questionGroup.getQuestions()) {
                    altered = altered || ensureEncrypted(
                        question::getText,
                        question::setText
                    );
                    CheckboxGroup checkboxGroup = question.getCheckboxGroup();
                    if(checkboxGroup == null) continue;
                    for(Checkbox checkbox: checkboxGroup.getCheckboxes()) {
                        altered = altered || ensureEncrypted(
                            checkbox::getText,
                            checkbox::setText
                        );
                    }
                }
            }
            for(SurveyResponse surveyResponse: survey.getSurveyResponses()) {
                for(Answer answer: surveyResponse.getAnswers()) {
                    altered = altered || ensureEncrypted(
                        answer::getText,
                        answer::setText
                    );
                }
            }
            if(altered) {
                surveyRepository.save(survey);
            }
        }
        surveyRepository.flush();
    }
    
    private boolean ensureEncrypted(
        Supplier<? extends EncryptedString> sup,
        Consumer<? super EncryptedString> con
    ) {
        final EncryptedString str = sup.get();
        if(str == null || str.isEncrypted()) return false;
        con.accept(new EncryptedString(
            cryptoService.encryptDB(str.getString()),
            true
        ));
        return true;
    }
}
