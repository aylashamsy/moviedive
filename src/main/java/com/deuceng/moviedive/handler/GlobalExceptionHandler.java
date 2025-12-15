package com.deuceng.moviedive.handler;

import com.deuceng.moviedive.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler
{

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ApiError> handleBaseException(BaseException exception, WebRequest request)
    {
        //Buradaki ex.getMessage aslında BaseException class'ı içinde super RuntimeException'a gönderdiğimiz mesaj
        return ResponseEntity.badRequest().body(createApiError(exception.getMessage(), request));
    }

    public <E> ApiError<E> createApiError(E message, WebRequest request)
    {
        ApiError <E>apiError = new ApiError();
        apiError.setStatus(HttpStatus.BAD_REQUEST.value());

        Exception exception = new Exception();
        exception.setHostName(getHostName());
        exception.setMessage(message);
        //ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Europe/Istanbul"));
        //exception.setCreateTime(now);
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
