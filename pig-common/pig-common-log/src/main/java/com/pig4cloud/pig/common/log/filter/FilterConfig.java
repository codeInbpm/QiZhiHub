package com.pig4cloud.pig.common.log.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName FilterConfig
 * @Description 过滤器配置类
 * @Author hanql
 * @Date 2025/2/16
 */
@Configuration
public class FilterConfig {


	/**
	 * 日志打印过滤器
	 * 注册 RequestLogFilter
	 * @return 过滤器注册配置
	 */
	@Bean
	public FilterRegistrationBean<RequestLogFilter> requestLogFilter() {
		FilterRegistrationBean<RequestLogFilter> registrationBean = new FilterRegistrationBean<>();
		registrationBean.setFilter(new RequestLogFilter());
		registrationBean.addUrlPatterns("/*"); // 设置需要过滤的 URL 模式，根据需求配置
		registrationBean.setOrder(-50);  // 后于Sleuth (-100)，MDC已填充
		registrationBean.setName("requestLogFilter");
		return registrationBean;
	}



}