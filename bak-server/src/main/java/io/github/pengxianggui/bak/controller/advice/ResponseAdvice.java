package io.github.pengxianggui.bak.controller.advice;

import io.github.pengxianggui.bak.controller.vo.Result;
import org.springframework.core.MethodParameter;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author pengxg
 * @date 2025-01-08 16:13
 */
@RestControllerAdvice
public class ResponseAdvice implements ResponseBodyAdvice<Object> {
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        return methodParameter.getDeclaringClass().getName().startsWith("io.github.pengxianggui.bak")
                || methodParameter.getDeclaringClass().getName().startsWith("io.github.pengxianggui.crud");
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof Result) {
            return body;
        } else if (body instanceof byte[] || body instanceof Resource) { //已做包装或二进制流则不做处理
            return body;
        }
        return Result.success(body);
    }
}
