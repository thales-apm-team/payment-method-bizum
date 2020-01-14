package com.payline.payment.bizum.utils.http;

import com.payline.payment.bizum.bean.configuration.RequestConfiguration;
import com.payline.payment.bizum.exception.InvalidDataException;
import com.payline.payment.bizum.exception.PluginException;
import com.payline.payment.bizum.utils.Constants;
import com.payline.payment.bizum.utils.PluginUtils;
import com.payline.payment.bizum.utils.properties.ConfigProperties;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.logger.LogManager;
import org.apache.http.HttpHeaders;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public class BizumHttpClient {

    private static final Logger LOGGER = LogManager.getLogger(BizumHttpClient.class);

    //Headers
    private static final String CONTENT_TYPE_VALUE = "application/xml";

    // Exceptions messages
    private static final String SERVICE_URL_ERROR = "Service URL is invalid";
    /**
     * The number of time the client must retry to send the request if it doesn't obtain a response.
     */
    private int retries;

    private HttpClient client;

    // --- Singleton Holder pattern + initialization BEGIN
    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    BizumHttpClient() {
            int connectionRequestTimeout;
            int connectTimeout;
            int socketTimeout;
            try {
                // request config timeouts (in seconds)
                ConfigProperties config = ConfigProperties.getInstance();
                connectionRequestTimeout = Integer.parseInt(config.get("http.connectionRequestTimeout"));
                connectTimeout = Integer.parseInt(config.get("http.connectTimeout"));
                socketTimeout = Integer.parseInt(config.get("http.socketTimeout"));

                // retries
                this.retries = Integer.parseInt(config.get("http.retries"));
            } catch (NumberFormatException e) {
                throw new PluginException("plugin error: http.* properties must be integers", e);
            }

            RequestConfig requestConfig = RequestConfig.custom()
                    .setConnectionRequestTimeout(connectionRequestTimeout * 1000)
                    .setConnectTimeout(connectTimeout * 1000)
                    .setSocketTimeout(socketTimeout * 1000)
                    .build();

            // instantiate Apache HTTP client
            this.client = HttpClientBuilder.create()
                    .useSystemProperties()
                    .setDefaultRequestConfig(requestConfig)
                    .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier()))
                    .build();
    }
    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    private static class Holder {
        private static final BizumHttpClient instance = new BizumHttpClient();
    }

    /**
     * ------------------------------------------------------------------------------------------------------------------
     */
    public static BizumHttpClient getInstance() {
        return Holder.instance;
    }
    // --- Singleton Holder pattern + initialization END

    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Send the request, with a retry system in case the client does not obtain a proper response from the server.
     *
     * @param httpRequest The request to send.
     * @return The response converted as a {@link StringResponse}.
     * @throws PluginException If an error repeatedly occurs and no proper response is obtained.
     */
    StringResponse execute(HttpRequestBase httpRequest) {
        StringResponse strResponse = null;
        int attempts = 1;

        while (strResponse == null && attempts <= this.retries) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Start call to partner API (attempt {}) :" + System.lineSeparator() + PluginUtils.requestToString(httpRequest), attempts);
            } else {
                LOGGER.info("Start call to partner API [{} {}] (attempt {})", httpRequest.getMethod(), httpRequest.getURI(), attempts);
            }
            try (CloseableHttpResponse httpResponse = (CloseableHttpResponse) this.client.execute(httpRequest)) {
                strResponse = StringResponse.fromHttpResponse(httpResponse);
            } catch (IOException e) {
                LOGGER.error("An error occurred during the HTTP call :", e);
                strResponse = null;
            } finally {
                attempts++;
            }
        }

        if (strResponse == null) {
            throw new PluginException("Failed to contact the partner API", FailureCause.COMMUNICATION_ERROR);
        }
        LOGGER.info("Response obtained from partner API [{} {}]", strResponse.getStatusCode(), strResponse.getStatusMessage());
        return strResponse;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Verify if API url are present
     *
     * @param requestConfiguration
     */
    private void verifyPartnerConfigurationURL(RequestConfiguration requestConfiguration) {
        if (requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.BIZUM_URL)== null) {
            throw new InvalidDataException("Missing API url from partner configuration (sentitive properties)");
        }

        if (requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.KEY) == null ||
                requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.KEY).getValue() == null) {
            throw new InvalidDataException("Missing client private key from partner configuration (sentitive properties)");
        }
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    void createStandardPayementBody(){

    }
    /**------------------------------------------------------------------------------------------------------------------*/
    void createDoubleFlowBody(){

    }
    /**------------------------------------------------------------------------------------------------------------------*/
    public Boolean verifyConnection(RequestConfiguration requestConfiguration){

        return false;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    /**
     * Manage Post API call
     * @param requestConfiguration
     * @param body
     * @return
     */
    public StringResponse post(RequestConfiguration requestConfiguration, String body){

        // Check if API url are present
        verifyPartnerConfigurationURL(requestConfiguration);

        String baseUrl = requestConfiguration.getPartnerConfiguration().getProperty(Constants.PartnerConfigurationKeys.BIZUM_URL);

        // Init request
        URI uri;

        try {
            // Add the createOrderId to the url
            uri = new URI(baseUrl);
        } catch (URISyntaxException e) {
            throw new InvalidDataException(SERVICE_URL_ERROR, e);
        }

        HttpPost httpPost = new HttpPost(uri);

        // Headers
        String keyHolder = requestConfiguration.getContractConfiguration().getProperty(Constants.ContractConfigurationKeys.KEY).getValue();
        Map<String, String> headers = new HashMap<>();
        headers.put(HttpHeaders.CONTENT_TYPE, CONTENT_TYPE_VALUE);

        for (Map.Entry<String, String> h : headers.entrySet()) {
            httpPost.setHeader(h.getKey(), h.getValue());
        }

        // Body
        if(body != null) {
            httpPost.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        }

        // Execute request
        return this.execute(httpPost);
    }
    /**------------------------------------------------------------------------------------------------------------------*/
}
