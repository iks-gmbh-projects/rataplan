package de.iks.rataplan.config;

import de.iks.rataplan.mapping.DecisionConverter;
import de.iks.rataplan.mapping.crypto.FromEncryptedStringConverter;
import de.iks.rataplan.mapping.crypto.ToEncryptedStringConverter;
import de.iks.rataplan.service.MockCryptoService;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Profile("integration")
@PropertySource("classpath:integration.properties")
@RequiredArgsConstructor
@TestConfiguration
public class IntegrationConfig {
    private final MockCryptoService mockCryptoService = new MockCryptoService();
    private final FromEncryptedStringConverter fromEncryptedStringConverter = new FromEncryptedStringConverter(
        mockCryptoService);
    private final ToEncryptedStringConverter toEncryptedStringConverter = new ToEncryptedStringConverter(
        mockCryptoService);
    
    @Bean
    public ModelMapper modelMapper(DecisionConverter decisionConverter) {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(decisionConverter.toDAO);
        mapper.addConverter(decisionConverter.toDTO);
        mapper.addConverter(toEncryptedStringConverter);
        mapper.addConverter(fromEncryptedStringConverter);
        return mapper;
    }
}
