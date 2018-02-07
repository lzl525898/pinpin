package com.pinpin.core.http;

import java.util.List;
import java.util.Map;

public class  ResponseWrapper <T> {
	private T responseBody;
	private int responseCode;
	private Map<String, List<String>> responseHeaders;
	private boolean requestSuccess; 
	public ResponseWrapper (int responseCode, T responseBody) {
		this.responseCode = responseCode;
		this.responseBody = responseBody;
	}
	
	public void setResponseHeaders(Map<String, List<String>> headers) {
		this.responseHeaders = headers;
	}
	
	public Map<String, List<String>> getResponseHeaders() {
		return this.responseHeaders;
	}
	
	public T getResponseBody() {
		return responseBody;
	}
	
	public int getResponseCode() {
		return this.responseCode;
	}
	
	public void setResponseCode(int code) {
		this.responseCode = code;
	}

	public boolean isRequestSuccess() {
		return requestSuccess;
	}

	public void setRequestSuccess(boolean requestSuccess) {
		this.requestSuccess = requestSuccess;
	}

	 
	
}
