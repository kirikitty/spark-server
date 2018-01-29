package com.pingan.spark.server.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.spark.launcher.SparkLauncher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pingan.spark.server.model.Response;
import com.pingan.spark.server.utils.ConfigUtils;

@RestController
public class SparkController {

	private static final Logger logger = LoggerFactory.getLogger(SparkController.class);

	@RequestMapping("/test")
	public String test() {
		return "Hello world";
	}

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public Response getConfig() {
		try {
			return Response.success(ConfigUtils.getConfiguration());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/config", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public Response setConfig(@RequestBody Map<String, Object> config) {
		try {
			ConfigUtils.setConfiguration(config);
			return Response.success(null);
		} catch (Exception e) {
			return Response.failure(e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/submit")
	public Response submit(@RequestBody Map<String, Object> params) {
		try {
			Map<String, Object> config = ConfigUtils.getConfiguration();
			String sparkHome = (String) params.get("sparkHome");
			if (StringUtils.isEmpty(sparkHome)) {
				sparkHome = (String) config.get(ConfigUtils.SPARK_HOME);
			}
			if (StringUtils.isEmpty(sparkHome)) {
				return Response.failure("sparkHome can't be null.");
			}

			String appJar = ConfigUtils.getUploadJarPath((String) params.get("jar"));
			if (StringUtils.isEmpty(appJar)) {
				appJar = ConfigUtils.getUploadJarPath((String) config.get(ConfigUtils.APP_JAR));
			}
			if (StringUtils.isEmpty(appJar)) {
				return Response.failure("jar can't be null.");
			}

			String mainClass = (String) params.get("mainClass");
			if (StringUtils.isEmpty(mainClass)) {
				return Response.failure("mainClass can't be null.");
			}

			String master = (String) params.get("master");
			if (StringUtils.isEmpty(master)) {
				master = (String) config.get(ConfigUtils.SPARK_MASTER);
			}
			if (StringUtils.isEmpty(master)) {
				return Response.failure("master can't be null.");
			}

			SparkLauncher launcher = new SparkLauncher();
			launcher.setSparkHome(sparkHome).setMainClass(mainClass).setMaster(master).setAppResource(appJar);
			List<String> appArgs = (List<String>) params.get("appArgs");
			if (!CollectionUtils.isEmpty(appArgs)) {
				launcher.addAppArgs(appArgs.toArray(new String[appArgs.size()]));
			}

			Map<String, String> sparkArgs = (Map<String, String>) config.get(ConfigUtils.SPARK_ARGS);
			sparkArgs = sparkArgs == null ? new HashMap<>() : sparkArgs;
			Map<String, String> tempArgs = (Map<String, String>) params.get("sparkArgs");
			if (!CollectionUtils.isEmpty(tempArgs)) {
				sparkArgs.putAll(tempArgs);
			}
			if (!CollectionUtils.isEmpty(sparkArgs)) {
				sparkArgs.forEach((k, v) -> launcher.addSparkArg(k, v));
			}

			List<String> dependentJars = (List<String>) config.get(ConfigUtils.DEPENDENT_JARS);
			dependentJars = dependentJars == null ? new ArrayList<>() : dependentJars;
			List<String> tempJars = (List<String>) params.get("dependentJars");
			if (!CollectionUtils.isEmpty(tempJars)) {
				dependentJars.addAll(tempJars);
			}
			if (!CollectionUtils.isEmpty(dependentJars)) {
				dependentJars.stream().map(jar -> ConfigUtils.getUploadJarPath(jar)).filter(jar -> jar != null)
						.map(jar -> jar.startsWith("/") ? "local:" + jar : jar).distinct()
						.forEach(jar -> launcher.addJar(jar));
			}

			launcher.launch();
			return Response.success(null);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return Response.failure(e.getMessage());
		}
	}

	@RequestMapping("/upload")
	public Response upload() {
		return Response.failure("Not implemented interface.");
	}
}
