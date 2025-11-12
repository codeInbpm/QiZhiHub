package com.pig4cloud.pig.common.log.filter;

import cn.hutool.core.util.StrUtil;
import com.pig4cloud.pig.common.log.filter.dto.RequestWrapper;
import com.pig4cloud.pig.common.log.filter.dto.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

/**
 * @ClassName RequestLogFilter
 * @Description 打印请求与响应日志
 * @Author hanql
 * @Date 2025/11/12
 */
@Slf4j
public class RequestLogFilter extends OncePerRequestFilter {

	/**
	 * 不打印日志的请求，可以在下面补充
	 *
	 * @param request 请求
	 * @return 返回是否应跳过日志打印
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		// 忽略 swagger 和其它静态资源请求
		return StrUtil.containsAny(request.getRequestURI(), "/swagger", "/static", "/favicon.ico", "/actuator", "/springfox");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		String url = Objects.requireNonNull(request).getRequestURI();
		String method = Objects.requireNonNull(request).getMethod();

		// 包装请求
		RequestWrapper requestWrapper = new RequestWrapper(request);
		StringBuilder requestParam = new StringBuilder();
		BufferedReader bufferedReader = requestWrapper.getReader();
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			requestParam.append(line);
		}

		// 包装响应
		ResponseWrapper responseWrapper = new ResponseWrapper(response);

		// 新增：构建完整参数（递归打印ParameterMap，避免数组toString()丑陋）
		StringBuilder fullParams = new StringBuilder();
		String bodyParam = requestParam.toString().trim();
		if ("GET".equalsIgnoreCase(method) || StrUtil.isBlank(bodyParam)) {
			// GET: 递归解析query params
			fullParams.append("Query Params: ");
			Map<String, String[]> paramMap = request.getParameterMap();
			if (!paramMap.isEmpty()) {
				paramMap.forEach((key, values) -> {
					if (values != null && values.length > 0) {
						String valueStr = String.join(",", values);  // 数组转逗号字符串
						fullParams.append(key).append("=").append(valueStr).append(", ");
					}
				});
				fullParams.delete(fullParams.length() - 2, fullParams.length());  // 去尾部", "
			} else {
				fullParams.append("None");
			}
		} else {
			// POST/PUT: 用body
			fullParams.append("Body: ").append(bodyParam);
		}
		// 脱敏敏感字段
		String paramsStr = fullParams.toString().replaceAll("(?i)(password|token|authorization)=[^,\\s]+", "$1=***");

		// 构成一条长日志，避免并发下日志错乱
		StringBuilder reqLog = new StringBuilder(320);
		// 日志参数
		List<Object> reqArgs = new ArrayList<>();
		reqLog.append("\n====================  请求开始  ====================\n");
		// 打印路由
		reqLog.append("===> {}:  {}\n");
		reqArgs.add(method);
		reqArgs.add(url);
		// 请求参数（用优化后paramsStr）
		reqLog.append(" 【请求参数】 {}\n");
		reqArgs.add(paramsStr);
		// 打印请求头
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String headerName = headers.nextElement();
			if ("request-id".equals(headerName)) continue;
			String headerValue = request.getHeader(headerName);
			reqLog.append(" 【请求头】  {}: {}\n");
			reqArgs.add(headerName);
			reqArgs.add(headerValue);
		}
		String requestId = request.getHeader("request-id");
		if (StrUtil.isNotBlank(requestId)) {
			reqLog.append(" 【请求头】  {}: {}\n");
			reqArgs.add("request-id");
			reqArgs.add(requestId);
		}
		// 获取traceId
		String traceId = MDC.get("traceId");
//		if (StrUtil.isBlank(traceId)) {
//			traceId = UUID.randomUUID().toString().replace("-", "");
//		}
		reqLog.append(" 【请求头】  {}: {}\n");
		reqArgs.add("traceId");
		reqArgs.add(traceId);

		reqLog.append("====================  请求结束  ====================\n");

		// 打印执行时间
		long startNs = System.currentTimeMillis();  // 微调：用millis更准
		log.info(reqLog.toString(), reqArgs.toArray());

		// 继续过滤链
		filterChain.doFilter(requestWrapper, responseWrapper);

		// 响应日志
		StringBuilder respLog = new StringBuilder(220);
		List<Object> respArgs = new ArrayList<>();
		respLog.append("\n====================  响应开始  ====================\n");
		long tookMs = System.currentTimeMillis() - startNs;  // 匹配startNs
		respLog.append("<=== {}: {} ({} ms)\n");
		respArgs.add(method);
		respArgs.add(url);
		respArgs.add(tookMs);
		respLog.append(" 【响应报文】 {}\n");

		// 获取响应内容类型（不变）
		String contentType = responseWrapper.getContentType();
		String resp = contentType;
		if (!(MediaType.APPLICATION_OCTET_STREAM_VALUE.equals(contentType) ||
				"application/octet-stream;charset=UTF-8".equals(contentType))) {
			byte[] data = responseWrapper.getResponseData();
			if (data.length > 1024) {  // 新增：截断大响应防洪水
				resp = new String(data, 0, 1024).replace("\r\n", "") + "... [truncated]";
			} else {
				resp = new String(data).replace("\r\n", "");
			}
		}
		respArgs.add(resp);

		// 打印响应日志
		respLog.append("====================  响应结束  ====================\n");
		log.info(respLog.toString(), respArgs.toArray());

		// 确保输出流刷新并关闭
		ServletOutputStream os = response.getOutputStream();
		os.write(responseWrapper.getResponseData());
		os.flush();
		os.close();
	}
}