package br.com.server.resource.handle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class DefaultControllerAdvice {

    @Autowired
    private MessageSource messageSource;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public Map<String, Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error(ex.getLocalizedMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    public Map<String, Object> handleConstraintExceptions(ConstraintViolationException ex) {
        Map<String, Object> errors = new HashMap<>();
        ex.getConstraintViolations().stream().forEach(error -> {
            String errorMessage = error.getMessage();
            errors.put(messageSource.getMessage("message", null, null), errorMessage);
        });
        log.error(ex.getLocalizedMessage());
        return errors;
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Map<String, Object> handleGenerateExceptions(Exception ex) {
        Map<String, Object> errors = new HashMap<>();
        errors.put(messageSource.getMessage("status.code", null, null), String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        errors.put(messageSource.getMessage("message", null, null), messageSource.getMessage("internal.server.error", null, null));
        log.error(ex.getLocalizedMessage());
        return errors;
    }

    @ExceptionHandler(FeignException.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> feignExceptions(FeignException ex) throws IOException {
        final Map<String, String> map = new ObjectMapper().readValue(ex.responseBody().get().array(), new TypeReference<Map<String, String>>() {});
        return new ResponseEntity<Map<String, String>>(map, HttpStatus.valueOf(ex.status()));
    }

}
