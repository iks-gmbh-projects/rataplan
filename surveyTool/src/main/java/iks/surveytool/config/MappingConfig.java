package iks.surveytool.config;

import iks.surveytool.mapping.SurveyConverter;
import iks.surveytool.mapping.SurveyResponseConverter;
import iks.surveytool.mapping.SurveyResponseDTOConverter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MappingConfig {

    private final SurveyConverter surveyConverter;
    private final SurveyResponseConverter surveyResponseConverter;
    private final SurveyResponseDTOConverter surveyResponseDTOConverter;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(surveyConverter);
        modelMapper.addConverter(surveyResponseConverter);
        modelMapper.addConverter(surveyResponseDTOConverter);
        return modelMapper;
    }
}
