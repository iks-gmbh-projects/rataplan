package iks.surveytool.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("https://umfragetool.herokuapp.com")
                .allowedHeaders("*");
        corsRegistry.addMapping("/**")
                .allowedOrigins("http://localhost:4200/")
                .allowedHeaders("*");
    }
}
