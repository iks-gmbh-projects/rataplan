package de.iks.rataplan.config;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

import org.springframework.context.annotation.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@Profile({"dev", "prod", "test", "integration"})
@Configuration
@ComponentScan(basePackages = "de.iks.rataplan")
@EnableTransactionManagement
public class AppConfig {
    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean dbConfig = new com.github.springtestdbunit.bean.DatabaseConfigBean();
        dbConfig.setDatatypeFactory(new org.dbunit.ext.h2.H2DataTypeFactory());
        return dbConfig;
    }
    
    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection(DataSource dataSource) {
        DatabaseDataSourceConnectionFactoryBean dbConnection = new DatabaseDataSourceConnectionFactoryBean(dataSource);
        dbConnection.setDatabaseConfig(dbUnitDatabaseConfig());
        return dbConnection;
    }
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
