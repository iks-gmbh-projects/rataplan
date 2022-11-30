package de.iks.rataplan.config;

import java.util.Objects;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import lombok.RequiredArgsConstructor;
//import org.hibernate.cfg.AvailableSettings;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
//import org.springframework.orm.hibernate5.SpringBeanContainer;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.github.springtestdbunit.bean.DatabaseConfigBean;
import com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean;

@Profile({"dev", "prod", "test", "integration"})
@Configuration
@PropertySource({"classpath:/application.properties"})
@ComponentScan(basePackages = "de.iks.rataplan")
@EnableTransactionManagement
@RequiredArgsConstructor
public class AppConfig {

    private final Environment environment;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(Objects.requireNonNull(environment.getProperty("JDBC_DATABASE_DRIVER")));
        dataSource.setUrl(environment.getProperty("JDBC_DATABASE_URL"));
        dataSource.setUsername(environment.getProperty("JDBC_DATABASE_USERNAME"));
        dataSource.setPassword(environment.getProperty("JDBC_DATABASE_PASSWORD"));
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(ConfigurableListableBeanFactory beanFactory) {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan(environment.getProperty("entitymanager.packagesToScan"));

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);

        entityManagerFactory.setJpaProperties(additionalHibernateProperties());
//       entityManagerFactory.getJpaPropertyMap().put(AvailableSettings.BEAN_CONTAINER, new SpringBeanContainer(beanFactory));
        return entityManagerFactory;
    }

    @Bean
    public Properties additionalHibernateProperties() {
        Properties additionalProperties = new Properties();
        additionalProperties.put("hibernate.dialect", environment.getProperty("hibernate.dialect"));
        additionalProperties.put("hibernate.show_sql", environment.getProperty("hibernate.show_sql"));
        additionalProperties.put("hibernate.hbm2ddl.auto", environment.getProperty("hibernate.hbm2ddl.auto"));
        return additionalProperties;
    }

    @Bean
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    @Bean
    public BeanPostProcessor persistenceTranslation() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

    @Bean
    public DatabaseConfigBean dbUnitDatabaseConfig() {
        DatabaseConfigBean dbConfig = new com.github.springtestdbunit.bean.DatabaseConfigBean();
        dbConfig.setDatatypeFactory(new org.dbunit.ext.h2.H2DataTypeFactory());
        return dbConfig;
    }

    @Bean
    public DatabaseDataSourceConnectionFactoryBean dbUnitDatabaseConnection() {
        DatabaseDataSourceConnectionFactoryBean dbConnection = new com.github.springtestdbunit.bean.DatabaseDataSourceConnectionFactoryBean(
            dataSource());
        dbConnection.setDatabaseConfig(dbUnitDatabaseConfig());
        return dbConnection;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
