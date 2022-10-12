package iks.surveytool.config;

import iks.surveytool.mapping.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class MappingConfig {

    private final SurveyConverter surveyConverter;
    private final ToZonedTimeConverter toZonedTimeConverter;
    private final ToInstantConverter toInstantConverter;
    private final SurveyResponseConverter surveyResponseConverter;
    private final SurveyResponseDTOConverter surveyResponseDTOConverter;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(surveyConverter);
        modelMapper.addConverter(toZonedTimeConverter);
        modelMapper.addConverter(toInstantConverter);
        modelMapper.addConverter(surveyResponseConverter);
        modelMapper.addConverter(surveyResponseDTOConverter);
        return modelMapper;
    }
}
