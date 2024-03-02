package com.api.farmingsoon.common.exception;

import com.api.farmingsoon.common.alert.discord.DiscordService;
import com.api.farmingsoon.common.response.Response;
import io.lettuce.core.RedisCommandTimeoutException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {
    private final DiscordService discordService;

    /**
     * 서비스 로직 도중 발생하는 에러들을 커스텀하여 응답값을 내려줍니다.
     * 디스코드로 에러메시지를 전송합니다.
     */
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(HttpServletRequest request, CustomException e){
        ErrorCode errorCode = e.getErrorCode();
        discordService.sendDiscordAlertLog(errorCode.toString(), errorCode.getMessage(), request);
        log.error(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getStatus()).body(Response.error(errorCode.getStatus(), errorCode.getMessage()));
    }

    /**
     * 바인딩 에러 json형식으로 에러가 있는 파라미터 값에 메시지를 담아 넘겨줍니다.
     *  itemPrice : 아이템 가격을 100원 이상 1,000,000,000원 이하여야 합니다.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(HttpServletRequest request,MethodArgumentNotValidException ex){
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors()
                .forEach(c -> errors.put(((FieldError) c).getField(), c.getDefaultMessage()));
        discordService.sendDiscordAlertLog(HttpStatus.BAD_REQUEST.toString(), errors.toString(), request);
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Response.error(HttpStatus.BAD_REQUEST, errors));
    }
    @ExceptionHandler({RedisCommandTimeoutException.class, QueryTimeoutException.class})
    public ResponseEntity<?> handleException(Exception exception){
        log.info("Redis response delay");
        return ResponseEntity.
                status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Response.error(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."));
    }
}
