package io.eliez.fintools.bootext;

import one.util.streamex.EntryStream;
import one.util.streamex.StreamEx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Date;
import java.util.Map;

@Configuration
public class ValidationConfiguration {

    @Bean
    MethodValidationPostProcessor methodValidationPostProcessor() {
        return new MethodValidationPostProcessor();
    }

    @ControllerAdvice
    @Component
    public static class CustomExceptionHandlers {
        @ExceptionHandler
        @ResponseBody
        @ResponseStatus(HttpStatus.BAD_REQUEST)
        public Map<String, Object> handleBadRequest(ConstraintViolationException exception, HttpServletRequest request) {
            return EntryStream.of(
                    "path", request.getServletPath(),
                    "error", HttpStatus.BAD_REQUEST.getReasonPhrase(),
                    "status", HttpStatus.BAD_REQUEST.value(),
                    "exception", exception.getClass().getName(),
                    "violations", StreamEx.of(exception.getConstraintViolations())
                            .map(ConstraintViolation::getMessage)
                            .toList(),
                    "timestamp", new Date()
            ).toMap();
        }
    }
}
