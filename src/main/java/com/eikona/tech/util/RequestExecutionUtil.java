package com.eikona.tech.util;

import static com.eikona.tech.constants.NumberConstants.FIFTEEN;
import static com.eikona.tech.constants.NumberConstants.TEN;
import static com.eikona.tech.constants.NumberConstants.THOUSAND;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

@Component
public class RequestExecutionUtil {
	

	public String executeHttpPostRequest(HttpPost request) throws Exception {
		String responeData = null;
	
			int timeout = THOUSAND;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * THOUSAND)
					.setConnectionRequestTimeout(timeout * THOUSAND).setSocketTimeout(timeout * THOUSAND).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpResponse response = httpclient.execute(request);
			responeData = EntityUtils.toString(response.getEntity());
		return responeData;
	}
	
	public String executeHttpPutRequest(HttpPut request) {
		String responeData = null;
		try {
			int timeout = TEN;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * THOUSAND)
					.setConnectionRequestTimeout(timeout * THOUSAND).setSocketTimeout(timeout * THOUSAND).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpResponse response = httpclient.execute(request);
			responeData = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responeData;
	}

	public String executeHttpGetRequest(HttpGet request) {
		String responeData = null;
		try {
			int timeout = FIFTEEN;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * THOUSAND)
					.setConnectionRequestTimeout(timeout * THOUSAND).setSocketTimeout(timeout * THOUSAND).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpResponse response = httpclient.execute(request);
			responeData = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responeData;

	}

	public String executeHttpDeleteRequest(HttpDelete request) {
		String responeData = null;
		try {
			int timeout = FIFTEEN;
			RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout * THOUSAND)
					.setConnectionRequestTimeout(timeout * THOUSAND).setSocketTimeout(timeout * THOUSAND).build();
			CloseableHttpClient httpclient = HttpClientBuilder.create().setDefaultRequestConfig(config).build();

			HttpResponse response = httpclient.execute(request);
			responeData = EntityUtils.toString(response.getEntity());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return responeData;
	}
	
	
}
