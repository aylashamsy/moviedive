package com.deuceng.moviedive.handler;

import com.deuceng.moviedive.exception.BaseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(BaseException exception, WebRequest request)
    {
        //Buradaki ex.getMessage aslında BaseException class'ı içinde super RuntimeException'a gönderdiğimiz mesaj
        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(), request));
    }

    // 2) @Valid patlayınca: MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, WebRequest request) {

        // field -> message map
        Map<String, String> errors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> errors.put(err.getField(), err.getDefaultMessage()));

        // Message içine direkt map koyuyoruz (ApiError<E> zaten generic)
        return ResponseEntity.badRequest().body(createApiError(errors, request));
    }

    // 3) DB constraint (unique, not-null vs): DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolation(DataIntegrityViolationException ex, WebRequest request) {

        // En spesifik mesajdan hangi constraint patladıysa yakalamaya çalış
        String most = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage() : "";

        // Varsayılan mesaj
        String friendly = "Database constraint ihlal edildi.";

        // username/email unique için tipik postgres constraint adları:
        // users_username_key, users_email_key (veya senin verdiğin isim)
        if (most.contains("users_username_key")) {
            friendly = "Username zaten mevcut";
        } else if (most.contains("users_email_key")) {
            friendly = "Email zaten mevcut.";
        } else if (most.toLowerCase().contains("duplicate key")) {
            friendly = "Duplicate değer. Bu kayıt zaten mevcut.";
        } else if (most.toLowerCase().contains("null value")) {
            friendly = "Gerekli bir alan eksik (NULL değerine izin verilmiyor).";
        }

        return ResponseEntity.badRequest().body(createApiError(friendly, request));
    }

    // 4) Hibernate DB constraint exception
    @ExceptionHandler(org.hibernate.exception.ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleHibernateConstraintViolation(org.hibernate.exception.ConstraintViolationException ex, WebRequest request) {
        // constraint name çoğu zaman burada olur
        String constraint = ex.getConstraintName();
        String friendly = "Constraint violated" + (constraint != null ? (": " + constraint) : ".");
        return ResponseEntity.badRequest().body(createApiError(friendly, request));
    }

    // 5)jakarta.validation.ConstraintViolationException
    // Genelde @RequestParam, @PathVariable validation gibi yerlerde çıkar.
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleJakartaConstraintViolation(jakarta.validation.ConstraintViolationException ex,WebRequest request) {

        Map<String, String> errors = new LinkedHashMap<>();
        ex.getConstraintViolations().forEach(v -> {
            // propertyPath: "createUser.username" gibi gelebilir
            errors.put(v.getPropertyPath().toString(), v.getMessage());
        });

        return ResponseEntity.badRequest().body(createApiError(errors, request));
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    public ResponseEntity<ApiError> handleInvalidDataAccessApiUsage(InvalidDataAccessApiUsageException ex, WebRequest request) {


        String friendly = "İstek işlenemedi: veritabanı işlemi hatalı kullanıldı.";

        return ResponseEntity.badRequest().body(createApiError(friendly, request));
    }

    // 6) BaseException
    public <E> ApiError<E> createApiError(E message, WebRequest request)
    {
        ApiError <E>apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());

        Exception exception = new Exception();
        exception.setHostName(getHostName());
        exception.setMessage(message);
        exception.setCeateTime((new Date()));
        exception.setPath(request.getDescription(false).substring(4));

        apiError.setException(exception);
        return apiError;
    }


    private String getHostName()
    {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            System.out.println("Host adresi alınırken bir hata oluştu");
        }
        return null;
    }
}
