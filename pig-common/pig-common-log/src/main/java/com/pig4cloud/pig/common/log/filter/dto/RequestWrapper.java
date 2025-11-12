package com.pig4cloud.pig.common.log.filter.dto;

import cn.hutool.http.ContentType;
import io.micrometer.core.instrument.util.IOUtils;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

/**
 * 自定义RequestWrapper
 */
public class RequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    /**
     * 复制request中的bufferedReader中的值
     *
     * @param request
     * @throws IOException
     */
    public RequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        this.body = getBodyString(request);
    }

    /**
     * 获取请求body
     *
     * @param request 请求
     * @return bufferedReader值
     */
    private byte[] getBodyString(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        StringBuilder bodyStr = new StringBuilder();

        if (StringUtils.isNotBlank(contentType) && (contentType.contains(ContentType.MULTIPART.toString()) ||
                contentType.contains(ContentType.FORM_URLENCODED.toString()))) {
            Enumeration<String> parameterNames = request.getParameterNames();

            while (parameterNames.hasMoreElements()) {
                String paramName = parameterNames.nextElement();
                bodyStr.append(paramName).append("=").append(request.getParameter(paramName)).append("&");
            }

            bodyStr = new StringBuilder(bodyStr.toString().endsWith("&") ? bodyStr.substring(0, bodyStr.length() - 1) : bodyStr.toString());
            return bodyStr.toString().getBytes();
        }else{
            return IOUtils.toString(request.getInputStream(), StandardCharsets.UTF_8).getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public BufferedReader getReader() throws IOException {
        // 返回缓存的请求体内容
        return new BufferedReader(new InputStreamReader(getInputStream()));
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 使用缓存的字节数组来返回输入流
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);

        return new ServletInputStream() {
            // 实现read()方法
            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }


            // 实现isFinished()方法
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

        };
    }

}