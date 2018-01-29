package com.pingan.spark.server.interceptor;

import org.apache.commons.lang.time.StopWatch;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerTimeInterceptor {

	private static final Logger logger = LoggerFactory.getLogger(ControllerTimeInterceptor.class);

	@Around("execution (* com.pingan.spark.server.controller.*Controller.*(..)) and @annotation(org.springframework.web.bind.annotation.RequestMapping)")
	public Object intercept(ProceedingJoinPoint pjp) throws Throwable {
		Object result = null;
		StopWatch clock = new StopWatch();
		clock.start();
		try {
			result = pjp.proceed();
		} catch (Throwable t) {
			clock.stop();
			logger.error("{}: failed, duration = {}", pjp.getSignature().toShortString(), clock.getTime());
			throw t;
		}
		clock.stop();
		logger.info("{}: finished, duration = {}", pjp.getSignature().toShortString(), clock.getTime());
		return result;
	}

}
