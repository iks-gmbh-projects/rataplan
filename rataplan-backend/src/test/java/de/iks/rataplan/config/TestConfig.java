package de.iks.rataplan.config;

import de.iks.rataplan.mapping.DecisionConverter;
import de.iks.rataplan.service.CryptoService;
import de.iks.rataplan.service.MockCryptoService;
import io.jsonwebtoken.SigningKeyResolver;
import lombok.RequiredArgsConstructor;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

@Profile("test")
@PropertySource("classpath:/test.properties")
@RequiredArgsConstructor
public class TestConfig {

	private final Environment environment;

    @MockBean
    private SigningKeyResolver keyResolver;
    
    @Bean
    @Primary
    public CryptoService mockCryptoService() {
        return new MockCryptoService();
    }
    
    @Bean
    public ModelMapper modelMapper(DecisionConverter decisionConverter, List<Converter<?, ?>> converters) {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(decisionConverter.toDAO);
        mapper.addConverter(decisionConverter.toDTO);
        converters.forEach(mapper::addConverter);
        return mapper;
    }

	@Bean
	public HttpServletResponse httpServletResponse() {
		return new MockHttpServletResponse();
	}
	
    @Bean
    public DataSource dataSource() {
        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
        return builder
                .setType(EmbeddedDatabaseType.H2)
                .build();
    }
    
    @Bean 
    public Properties additionalHibernateProperties() {
    	Properties additionalProperties = new Properties();
        additionalProperties.put("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        additionalProperties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        additionalProperties.put("hibernate.hbm2ddl.auto", "validate");
        return additionalProperties;
    }
   
    @PostConstruct
	void started() {
		TimeZone.setDefault(TimeZone.getTimeZone("GMT+2")); 
	}
    
}
