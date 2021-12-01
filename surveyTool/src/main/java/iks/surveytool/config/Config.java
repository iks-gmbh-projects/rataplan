package iks.surveytool.config;

import iks.surveytool.mapping.SurveyConverter;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class Config {

    private final SurveyConverter surveyConverter;

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.addConverter(surveyConverter.toDAO);
        return modelMapper;
    }
}
