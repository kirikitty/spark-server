package com.kiri.spark.server.test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class TestServer {

	private static final Logger logger = LoggerFactory.getLogger(TestServer.class);

//	@Test
	public void testConfig() throws UnsupportedEncodingException {
		String url = "http://localhost:9000/spark/config";
		
		Map<String, Object> config = new HashMap<>();
		config.put("username", "kiri");
		config.put("labels", Arrays.asList("TestPerson", "TestWifi"));
		String resp = HttpUtils.postJson(url, JSON.toJSONString(config));
		logger.info("write: {}", resp);
		
		resp = HttpUtils.get(url);
		logger.info("get: {}", resp);
	}
	
	@Test
	public void testSubmit() {
		String url = "http://localhost:9000/spark/submit";
		
		Map<String, Object> params = new HashMap<>();
		params.put("master", "spark://127.0.0.1:7077");
		params.put("mainClass", "com.kiri.spark.apps.SimpleApp");
		String resp = HttpUtils.postJson(url, JSON.toJSONString(params));
		logger.info("submit: {}", resp);
	}
}
