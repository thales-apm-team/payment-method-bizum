package com.payline.payment.bizum.utils.http;

import com.payline.payment.bizum.exception.PluginException;
import com.payline.payment.bizum.utils.Constants;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.payline.payment.bizum.utils.http.HttpTestUtils.mockHttpResponse;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class BizumHttpClientTest {

    @InjectMocks
    @Spy
    private BizumHttpClient bizumHttpClient;
    @Mock
    private CloseableHttpClient http;

    @BeforeEach
    void setup() {
        // Init tested instance and inject mocks
        bizumHttpClient = new BizumHttpClient();
        MockitoAnnotations.initMocks(this);
    }
    // --- Test SharegroopHttpClient#execute ---

    @Test
    void execute_nominal() throws IOException {
        // given: a properly formatted request, which gets a proper response
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        int expectedStatusCode = 200;
        String expectedStatusMessage = "OK";
        String expectedContent = "{\"content\":\"fake\"}";
        doReturn(mockHttpResponse(expectedStatusCode, expectedStatusMessage, expectedContent, null))
                .when(http).execute(request);

        // when: sending the request
        StringResponse stringResponse = bizumHttpClient.execute(request);

        // then: the content of the StringResponse reflects the content of the HTTP response
        assertNotNull(stringResponse);
        assertEquals(expectedStatusCode, stringResponse.getStatusCode());
        assertEquals(expectedStatusMessage, stringResponse.getStatusMessage());
        assertEquals(expectedContent, stringResponse.getContent());
    }

    @Test
    void execute_retry() throws IOException {
        // given: the first 2 requests end up in timeout, the third request gets a response
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        when(http.execute(request))
                .thenThrow(ConnectTimeoutException.class)
                .thenThrow(ConnectTimeoutException.class)
                .thenReturn(mockHttpResponse(200, "OK", "content", null));

        // when: sending the request
        StringResponse stringResponse = bizumHttpClient.execute(request);

        // then: the client finally gets the response
        assertNotNull(stringResponse);
    }

    @Test
    void execute_retryFail() throws IOException {
        // given: a request which always gets an exception
        HttpGet request = new HttpGet("http://domain.test.fr/endpoint");
        doThrow(IOException.class).when(http).execute(request);

        // when: sending the request, a PluginException is thrown
        assertThrows(PluginException.class, () -> bizumHttpClient.execute(request));
    }

    @Test
    void execute_invalidResponse() throws IOException {
        // given: a request that gets an invalid response (null)
        HttpGet request = new HttpGet("http://domain.test.fr/malfunctioning-endpoint");
        doReturn(null).when(http).execute(request);

        // when: sending the request, a PluginException is thrown
        assertThrows(PluginException.class, () -> bizumHttpClient.execute(request));
    }
    // --- Test SharegroopHttpClient#CreateOrder ---


    static PartnerConfiguration anInvalidPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.BIZUM_URL, "://sis-t.redsys.es:25443/sis/realizarPago");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

}
