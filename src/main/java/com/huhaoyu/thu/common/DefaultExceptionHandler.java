package com.huhaoyu.thu.common;

import org.apache.commons.lang3.CharEncoding;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * Created by huhaoyu
 * Created On 2017/2/5 下午12:24.
 */

@RestControllerAdvice
public class DefaultExceptionHandler {

    @ExceptionHandler({UnacceptableParamException.class})
    @ResponseStatus(HttpStatus.OK)
    public Map processIncorrectParameterException(UnacceptableParamException e, HttpServletResponse response) {
        setResponseAsJson(response);
        return e.createResponse().createResponseMap(e.getMessage());
    }

    @ExceptionHandler({ServerErrorException.class})
    @ResponseStatus(HttpStatus.OK)
    public Map processServerErrorException(ServerErrorException e, HttpServletResponse response) {
        setResponseAsJson(response);
        return e.createResponse().createResponseMap(e.getMessage());
    }

    @ExceptionHandler({UnauthorizedException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Object processUnauthorizedException(UnauthorizedException e, HttpServletResponse response) {
        setResponseAsJson(response);
        return ResponseEntity.ok(e.createResponse().createResponseMap(e.getMessage()));
    }

    private void setResponseAsJson(HttpServletResponse response) {
        response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
        response.setCharacterEncoding(CharEncoding.UTF_8);
    }

}
