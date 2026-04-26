package cn.edu.sdu.java.server.configs;

import cn.edu.sdu.java.server.payload.response.DataResponse;
import cn.edu.sdu.java.server.util.CommonMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DataResponse handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Bad request: {}", e.getMessage());
        return CommonMethod.getReturnMessageError(e.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DataResponse handleNullPointerException(NullPointerException e) {
        log.error("Null pointer error", e);
        return CommonMethod.getReturnMessageError("数据处理错误");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DataResponse handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("参数验证失败");
        log.warn("Validation error: {}", message);
        return CommonMethod.getReturnMessageError(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DataResponse handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        log.warn("Malformed request: {}", e.getMessage());
        return CommonMethod.getReturnMessageError("请求格式错误");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DataResponse handleMissingParameter(MissingServletRequestParameterException e) {
        log.warn("Missing parameter: {}", e.getMessage());
        return CommonMethod.getReturnMessageError("缺少必要参数: " + e.getParameterName());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public DataResponse handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        log.warn("Type mismatch: {}", e.getMessage());
        return CommonMethod.getReturnMessageError("参数类型错误");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public DataResponse handleNoResourceFound(NoResourceFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());
        return CommonMethod.getReturnMessageError("资源不存在");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public DataResponse handleGenericException(Exception e) {
        log.error("Unexpected error", e);
        return CommonMethod.getReturnMessageError("服务器内部错误");
    }
}
