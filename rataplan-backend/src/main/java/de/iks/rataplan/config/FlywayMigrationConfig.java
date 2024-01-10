package de.iks.rataplan.config;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.flywaydb.core.Flyway;
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationInitializer;
import org.springframework.boot.autoconfigure.orm.jpa.EntityManagerFactoryDependsOnPostProcessor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Necessary until flyway can be updated to version expected by spring 2.x.x
 */
@Configuration
@ConfigurationProperties("flyway")
@Setter
@Slf4j
public class FlywayMigrationConfig {
    private String[] locations = new String[0];
    @Bean
    public Flyway flyway(DataSource datasource) {
        Flyway flyway = new Flyway();
        flyway.setDataSource(datasource);
        flyway.setLocations(locations);
        return flyway;
    }
    
    @Bean
    public FlywayMigrationInitializer flywayInitializer(Flyway flyway) {
        return new FlywayMigrationInitializer(flyway);
    }
    
    @Configuration
    public static class FlywayInitializerJpaDependencyConfiguration extends EntityManagerFactoryDependsOnPostProcessor {
        public FlywayInitializerJpaDependencyConfiguration() {
            super(FlywayMigrationInitializer.class);
        }
    }
}
