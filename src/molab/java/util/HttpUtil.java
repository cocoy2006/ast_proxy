package molab.java.util;

import java.io.IOException;
import java.util.logging.Logger;

import org.apache.http.HttpHost;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

public class HttpUtil {

	private static final Logger log = Logger.getLogger(HttpUtil.class.getName());
	private static final String SERVER_HOST = "http://alog.umeng.com/app_logs";
	private static final int TIMEOUT = 5000;

	public static boolean check(String hostname, Integer port) {
		HttpHost host = new HttpHost(hostname, port);
		DefaultProxyRoutePlanner routePlanner = 
				new DefaultProxyRoutePlanner(host);
		CloseableHttpClient httpclient = HttpClients.custom()
				.setRoutePlanner(routePlanner).build();

		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(TIMEOUT).setConnectTimeout(TIMEOUT).build();

		HttpGet httpget = new HttpGet(SERVER_HOST);
		httpget.setConfig(requestConfig);

		try {
			CloseableHttpResponse response = httpclient.execute(httpget);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				return true;
			}
		} catch (IOException e) {
//			log.severe(e.getMessage());
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				log.severe(e.getMessage());
			}
		}
		return false;
	}

}
