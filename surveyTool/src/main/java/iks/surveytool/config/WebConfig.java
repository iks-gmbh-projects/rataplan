package iks.surveytool.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedMethods("HEAD", "GET", "POST", "PUT")
                .allowedOrigins("http://localhost:4200/", "https://test.drumdibum.de/", "https://drumdibum.de/", "https://www.drumdibum.de/")
                .allowCredentials(true)
                .allowedHeaders("*");
    }


}
