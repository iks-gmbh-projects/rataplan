package de.iks.rataplan.config;

import de.iks.rataplan.mapping.DecisionConverter;
import lombok.RequiredArgsConstructor;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.google.gson.Gson;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.*;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;

import java.util.List;

@Profile({"dev", "prod", "test", "integration"})
@Configuration
@PropertySource({"classpath:/application.properties"})
@ComponentScan(basePackages = "de.iks.rataplan")
@EnableTransactionManagement
@RequiredArgsConstructor
public class AppConfig {
    
    @Bean
    public BeanPostProcessor persistenceTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }
    
    /**
     * ModelMapper for mapping between DAO and DTO beans
     *
     * toDAO and toDTO converters are just for mapping the VoteDecisions.
     * The toDAO converter accesses the database to get the options
     * according to the id's. Keep this in mind when writing tests!
     *
     * @return ModelMapper instance
     */
    @Bean
    public ModelMapper modelMapper(DecisionConverter decisionConverter, List<Converter<?, ?>> converters) {
        ModelMapper mapper = new ModelMapper();
        mapper.addConverter(decisionConverter.toDAO);
        mapper.addConverter(decisionConverter.toDTO);
        converters.forEach(mapper::addConverter);
        return mapper;
    }
    
    @Bean
    public TemplateEngine emailTemplateEngine() {
        final SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(htmlTemplateResolver());
        return templateEngine;
    }
    
    private ITemplateResolver htmlTemplateResolver() {
        final ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setOrder(2);
        templateResolver.setPrefix("/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);
        return templateResolver;
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean dbConfig = new com.github.springtestdbunit.bean.DatabaseConfigBean();
        dbConfig.setDatatypeFactory(new org.dbunit.ext.h2.H2DataTypeFactory());
        return dbConfig;
    }
    
    @Bean
    public Gson gson() {
        return new Gson();
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
