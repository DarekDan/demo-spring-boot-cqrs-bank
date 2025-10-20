package github.darekdan.demospringbootcqrsbank.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.server.i18n.AcceptHeaderLocaleContextResolver;

import java.util.List;
import java.util.Locale;

@Configuration
public class LocalizationConfiguration {
    
    @Bean
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = 
            new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setDefaultLocale(Locale.ENGLISH);
        messageSource.setCacheSeconds(3600); // Cache for 1 hour
        return messageSource;
    }
    
    @Bean
    public AcceptHeaderLocaleContextResolver localeContextResolver() {
        AcceptHeaderLocaleContextResolver resolver = new AcceptHeaderLocaleContextResolver();
        resolver.setSupportedLocales(List.of(
            Locale.ENGLISH,
            new Locale("pl"), // Polish
            new Locale("es"), // Spanish
            new Locale("de")  // German
        ));
        resolver.setDefaultLocale(Locale.ENGLISH);
        return resolver;
    }
}
