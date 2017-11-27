package com.inovision.apitest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

import com.inovision.apitest.model.Host;
import com.inovision.apitest.model.HttpContentType;
import com.inovision.apitest.model.HttpHeader;
import com.inovision.apitest.model.TestCase;
import com.inovision.apitest.model.TestResult;

@Component
public class RestHttpClientImpl implements RestHttpClient {

	private Logger logger = Logger.getLogger(RestHttpClientImpl.class);
	
	private int maxHttpConnections;
	private int httpConnectionTimeout;
	private int socketTimeout;
	
	private PoolingHttpClientConnectionManager httpConnMgr;
	private HttpClientBuilder clientBuilder;
	
	public RestHttpClientImpl() throws Exception {
		//initialize();
	}
	
	@Value("${http.maxconnections}")
	public void setMaxHttpConnections(int maxHttpConnections) {
		this.maxHttpConnections = maxHttpConnections;
	}
	
	@Value("${http.connectiontimeout}")
	public void setHttpConnectionTimeout(int httpConnectionTimeout) {
		this.httpConnectionTimeout = httpConnectionTimeout;
	}
	
	@Value("${net.sockettimeout}")
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	
	@PostConstruct
	private void initialize() throws Exception {
		logger.info("Initializing RestHttpClient");
		
		SSLContextBuilder ctxBuilder = new SSLContextBuilder();
		ctxBuilder.loadTrustMaterial(null, new TrustStrategy() {
			
			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		});
		SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(ctxBuilder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);
		Registry<ConnectionSocketFactory> socketFactoryRegistry = 
				RegistryBuilder.<ConnectionSocketFactory>create()
				.register("https", sslSocketFactory)
				.register("http", PlainConnectionSocketFactory.getSocketFactory()).build();
		
		httpConnMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		httpConnMgr.setDefaultMaxPerRoute(maxHttpConnections);
		httpConnMgr.setMaxTotal(maxHttpConnections);
		
		RequestConfig requestConfig = RequestConfig.custom()
										.setConnectTimeout(httpConnectionTimeout)
										.setProxy(null)
										.setSocketTimeout(socketTimeout)
										.build();
		clientBuilder = HttpClientBuilder.create();
		clientBuilder.setConnectionManager(httpConnMgr);
		clientBuilder.setDefaultRequestConfig(requestConfig);
		List<Header> headers = new ArrayList<Header>();
		headers.add(new BasicHeader("Accept", "application/json"));
		//headers.add(new BasicHeader("Accept", "application/xml"));
		//headers.add(new BasicHeader("Accept", "text/html"));
		headers.add(new BasicHeader("Accept", "text/plain"));
		headers.add(new BasicHeader("Content-Type", "application/json"));
		clientBuilder.setDefaultHeaders(headers);
	}
	
	public CloseableHttpClient getHttpClient() {
		return clientBuilder.build();
	}
	

	/* (non-Javadoc)
	 * @see com.cox.apitest.util.RestHttpClient#shutdown()
	 */
	@Override
	public void shutdown() {
		httpConnMgr.shutdown();
	}
	
	/* (non-Javadoc)
	 * @see com.cox.apitest.util.RestHttpClient#doHttpRequest(com.cox.apitest.model.TestCase, com.cox.apitest.model.Host)
	 */
	@Override
	public TestResult doHttpRequest(TestCase testCase, Host host, List<HttpHeader> headers) throws Exception {
		if(HttpMethod.GET.equals(testCase.getMethod())) {
			return doHttpGet(testCase, host, headers);
		} else if(HttpMethod.POST.equals(testCase.getMethod())) {
			return doHttpPost(testCase, host, headers);
		} else if(HttpMethod.DELETE.equals(testCase.getMethod())) {
			return doHttpDelete(testCase, host, headers);
		} else if(HttpMethod.PUT.equals(testCase.getMethod())) {
			return doHttpPut(testCase, host, headers);
		} else {
			throw new Exception("Invalid Http Method");
		}
	}
	
	public TestResult doHttpGet(TestCase testCase, Host host, List<HttpHeader> headers) throws Exception {
		HttpGet httpGet = new HttpGet(makeUrl(testCase, host));
		return executeHttpCommand(httpGet, testCase, headers);
	}
	
	public TestResult doHttpDelete(TestCase testCase, Host host, List<HttpHeader> headers) throws Exception {
		HttpDelete httpdelete = new HttpDelete(makeUrl(testCase, host));
		return executeHttpCommand(httpdelete, testCase, headers);
	}

	public TestResult doHttpPost(TestCase testCase, Host host, List<HttpHeader> headers) throws Exception {
		HttpPost httpPost = new HttpPost(makeUrl(testCase, host));
		return executeHttpCommand(httpPost, testCase, headers);
	}

	public TestResult doHttpPut(TestCase testCase, Host host, List<HttpHeader> headers) throws Exception {
		HttpPut httpput = new HttpPut(makeUrl(testCase, host));
		return executeHttpCommand(httpput, testCase, headers);
	}
	
	private TestResult executeHttpCommand(HttpRequestBase request, TestCase testCase, List<HttpHeader> headers) throws Exception {
		TestResult response = new TestResult();
		if(request instanceof HttpPost || request instanceof HttpPut) {
			HttpEntity entity = new StringEntity(testCase.getData());
			HttpEntityEnclosingRequestBase req = (HttpEntityEnclosingRequestBase)request;
			req.setEntity(entity);
		}
		CloseableHttpClient httpClient = getHttpClient();
		if(headers != null && !headers.isEmpty()) {
			for(HttpHeader h : headers) {
				request.addHeader(h.getName(), h.getValue());
			}
		}
		try {
			CloseableHttpResponse resp = httpClient.execute(request);
	 		response.setReturnCode(resp.getStatusLine().getStatusCode());
			if(resp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
	 			response.setSuccess(true);
			} else {
				logger.error("Possible error in executing HTTP command : " + resp.getStatusLine());
				response.setSuccess(false);
				response.setError(resp.getStatusLine().toString());
			}
	 		Header contentType = resp.getEntity().getContentType();
	 		response.setContentType(HttpContentType.fromType(contentType.getValue()));
	 		response.setResult(getResponse(resp.getEntity()));
		} catch(Exception e) {
			logger.error("Error in executing HTTP Command", e);
			response.setSuccess(false);
			response.setError(e.getMessage());
		} catch(Throwable t) {
			logger.error("Error in executing HTTP Command", t);
			response.setSuccess(false);
			response.setError(t.getMessage());
		}
	 	return response;		
	}
	
	private String getResponse(HttpEntity entity) throws IllegalStateException, IOException {
		StringBuilder buf = new StringBuilder();
		InputStream is = entity.getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String str = null;
		while((str=reader.readLine()) != null) {
			buf.append(str);
		}
		String response = buf.toString();
		response = response.replaceAll("\",\"", "\", \"");
		response = response.replaceAll("\":", "\": ");
		response = response.replaceAll("},", "}, ");
		return response;
	}
	
	private String makeUrl(TestCase testCase, Host host) {
		return String.format("%1$s%2$s", host.toUrlFormat(), testCase.getRestUrl());
	}
	
}
