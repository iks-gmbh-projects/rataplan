package iks.surveytool.config;

import iks.surveytool.mapping.AnswerConverter;
import iks.surveytool.mapping.SurveyConverter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MappingConfig {

    private final SurveyConverter surveyConverter;
    private final AnswerConverter answerConverter;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(surveyConverter.toDAO);
        modelMapper.addConverter(answerConverter.toDAO);
        return modelMapper;
    }
}
