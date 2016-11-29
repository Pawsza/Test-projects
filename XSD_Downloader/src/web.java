import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.ProxyAuthenticationStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;

public class web {
	public static synchronized CredentialsProvider prepareCredentials(String proxyHost, String proxyDomain, int proxyPort, final String proxyUserName, final String proxyPassword) {
		CredentialsProvider credsProvider = null;
		if (proxyHost != null && !proxyHost.isEmpty() && proxyUserName != null && !proxyUserName.isEmpty()) {
			credsProvider = new BasicCredentialsProvider();
			if (proxyDomain != null && !proxyDomain.isEmpty()) {
				credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort, AuthScope.ANY_HOST, "ntlm"), new NTCredentials(proxyUserName, proxyPassword, "", proxyDomain));
			} else {
				credsProvider.setCredentials(new AuthScope(proxyHost, proxyPort), new UsernamePasswordCredentials(proxyUserName, proxyPassword));
			}
		}
		return credsProvider;
	}

	public static synchronized CloseableHttpClient prepareServiceClient(CredentialsProvider credsProvider, String proxyHost, int proxyPort, final String proxyUserName)
			throws NoSuchAlgorithmException, KeyManagementException {

		HttpClientBuilder clientBuilder = HttpClientBuilder.create();
		if (proxyHost != null && !proxyHost.isEmpty()) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort);

			clientBuilder.useSystemProperties();
			clientBuilder.setProxy(proxy);
			clientBuilder.setRoutePlanner(new DefaultProxyRoutePlanner(proxy));
			// clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort, "https"));

			if (proxyUserName != null && !proxyUserName.isEmpty()) {
				clientBuilder.setDefaultCredentialsProvider(credsProvider);
				clientBuilder.setProxyAuthenticationStrategy(new ProxyAuthenticationStrategy());
			} else {
				System.setProperty("java.net.useSystemProxies", "true");
				System.setProperty("https.proxyHost", proxyHost);
				System.setProperty("https.proxyPort", Integer.toString(proxyPort));
			}
		}

		SSLContext sc = SSLContext.getInstance("SSLv3");
		sc.init(null, new TrustManager[] { trm }, null);
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sc);

		CloseableHttpClient client = clientBuilder
				// .setConnectionTimeToLive(20, TimeUnit.SECONDS)
				// .setMaxConnTotal(400).setMaxConnPerRoute(400)
				// .setDefaultRequestConfig(RequestConfig.custom().build())
				.setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(7000).setConnectTimeout(7000).setConnectionRequestTimeout(7000).build())
				// .setRetryHandler(new DefaultHttpRequestRetryHandler(5, true))
				.setSSLSocketFactory(sslsf).build();
		return client;
	}

	private static TrustManager trm = new X509TrustManager() {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(X509Certificate[] certs, String authType) {
		}
	};
}
