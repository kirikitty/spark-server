package com.pingan.spark.server.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Response {

	private int code;
	private String msg;
	private Object data;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public static Response success(Object data) {
		Response resp = new Response();
		resp.setCode(0);
		resp.setMsg("success");
		resp.setData(data);
		return resp;
	}

	public static Response failure(String message) {
		return failure(-1, message);
	}

	public static Response failure(int code, String message) {
		Response resp = new Response();
		resp.setCode(code);
		resp.setMsg(message);
		return resp;
	}
}
