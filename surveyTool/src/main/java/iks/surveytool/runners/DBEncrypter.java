package iks.surveytool.runners;

import iks.surveytool.entities.*;
import iks.surveytool.repositories.*;
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
    private final QuestionGroupRepository questionGroupRepository;
    private final QuestionRepository questionRepository;
    private final CheckboxRepository checkboxRepository;
    private final AnswerRepository answerRepository;
    private final CryptoService cryptoService;
    
    @Override
    public void run(ApplicationArguments args) {
        surveyRepository.findAllUnencrypted()
            .peek(survey -> {
                ensureEncrypted(survey::getName, survey::setName);
                ensureEncrypted(survey::getDescription, survey::setDescription);
            })
            .forEach(surveyRepository::save);
        surveyRepository.flush();
        questionGroupRepository.findAllUnencrypted()
            .peek(questionGroup -> ensureEncrypted(questionGroup::getTitle, questionGroup::setTitle))
            .forEach(questionGroupRepository::save);
        questionGroupRepository.flush();
        questionRepository.findAllUnencrypted()
            .peek(question -> ensureEncrypted(question::getText, question::setText))
            .forEach(questionRepository::save);
        questionRepository.flush();
        checkboxRepository.findAllUnencrypted()
            .peek(checkbox -> ensureEncrypted(checkbox::getText, checkbox::setText))
            .forEach(checkboxRepository::save);
        checkboxRepository.flush();
        answerRepository.findAllUnencrypted()
            .peek(answer -> ensureEncrypted(answer::getText, answer::setText))
            .forEach(answerRepository::save);
        answerRepository.flush();
    }
    
    private void ensureEncrypted(
        Supplier<? extends EncryptedString> sup,
        Consumer<? super EncryptedString> con
    ) {
        final EncryptedString str = sup.get();
        if (str == null || str.isEncrypted()) return;
        con.accept(new EncryptedString(
            cryptoService.encryptDB(str.getString()),
            true
        ));
    }
}
