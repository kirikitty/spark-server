package com.kiri.spark.server.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class ConfigUtils {

	private static final Logger logger = LoggerFactory.getLogger(ConfigUtils.class);

	public static final String SPARK_HOME = "spark.home";
	public static final String APP_JAR = "spark.appJar";
	public static final String SPARK_MASTER = "spark.master";
	public static final String SPARK_ARGS = "spark.sparkArgs";
	public static final String DEPENDENT_JARS = "spark.dependentJars";

	private static Map<String, Object> getDefaultConfiguration() {
		try (InputStream is = ConfigUtils.class.getResourceAsStream("/config.json")) {
			return JSON.parseObject(is, null);
		} catch (Exception e) {
			logger.error("load config.json failed.", e);
		}
		return new HashMap<>();
	}

	private static File getConfigFile() {
		File dir = new File(System.getProperty("user.home"), "/spark/server");
		if (!dir.exists()) {
			dir.mkdirs();
		}

		File f = new File(dir, "/config.json");
		return f;
	}

	public static String getUploadJarPath(String jarName) {
		if (jarName == null || jarName.contains(":") || jarName.contains("/")) {
			return jarName;
		}

		File f = new File(System.getProperty("user.home"), "/spark/jars/" + jarName);
		if (f.exists()) {
			return f.getAbsolutePath();
		}

		return null;
	}

	public static Map<String, Object> getConfiguration() {
		Map<String, Object> config = getDefaultConfiguration();
		File file = getConfigFile();
		if (file.exists()) {
			try (BufferedReader br = new BufferedReader(new FileReader(file))) {
				JSONObject json = JSON.parseObject(br.readLine());
				config.putAll(json);
			} catch (Exception e) {
				logger.error("read custom configuration failed.", e);
			}
		}
		return config;
	}

	public static void setConfiguration(Map<String, Object> config) throws Exception {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(getConfigFile()))) {
			bw.write(JSON.toJSONString(config));
		}
	}
}
