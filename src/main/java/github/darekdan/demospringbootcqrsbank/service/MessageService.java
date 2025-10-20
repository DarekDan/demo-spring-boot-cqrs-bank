package github.darekdan.demospringbootcqrsbank.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.Locale;

/**
 * Service for retrieving localized messages.
 */
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageSource messageSource;

    /**
     * Get localized message by key with parameters.
     */
    public String getMessage(String key, Object... args) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(key, args, locale);
    }

}
