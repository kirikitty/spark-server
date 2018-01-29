package com.pingan.spark.server.test;

import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class HttpUtils {
	public static final String get(String url) {
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			CloseableHttpResponse resp = client.execute(new HttpGet(url));
			if (resp.getStatusLine().getStatusCode() != 200) {
				return "status code: " + resp.getStatusLine().getStatusCode();
			}
			String s = IOUtils.toString(resp.getEntity().getContent());
			resp.close();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final String postJson(String url, String json) {
		CloseableHttpClient client = HttpClients.createDefault();
		try {
			HttpPost post = new HttpPost(url);
			post.setHeader("Content-Type", "application/json");
			post.setEntity(new StringEntity(json, "utf-8"));
			CloseableHttpResponse resp = client.execute(post);
			if (resp.getStatusLine().getStatusCode() != 200) {
				return "status code: " + resp.getStatusLine().getStatusCode();
			}
			String s = IOUtils.toString(resp.getEntity().getContent());
			resp.close();
			return s;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
