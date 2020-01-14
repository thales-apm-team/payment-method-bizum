package com.payline.payment.bizum;

import com.payline.payment.bizum.utils.Constants;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.Environment;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings("WeakerAccess")
public class MockUtils {
    /**
     * Generate a valid {@link Environment}.
     */
    public static Environment anEnvironment() {
        return new Environment("http://notificationURL.com",
                "http://redirectionURL.com",
                "http://redirectionCancelURL.com",
                true);
    }

    /**
     * Generate a valid {@link PartnerConfiguration}.
     */
    public static PartnerConfiguration aPartnerConfiguration() {
        Map<String, String> partnerConfigurationMap = new HashMap<>();

        partnerConfigurationMap.put(Constants.PartnerConfigurationKeys.BIZUM_URL, "https://sis-t.redsys.es:25443/sis/realizarPago");

        Map<String, String> sensitiveConfigurationMap = new HashMap<>();

        return new PartnerConfiguration(partnerConfigurationMap, sensitiveConfigurationMap);
    }

    /**
     * Generate a valid {@link PaymentFormLogoRequest}.
     */
    public static PaymentFormLogoRequest aPaymentFormLogoRequest() {
        return PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withContractConfiguration(aContractConfiguration())
                .withEnvironment(anEnvironment())
                .withPartnerConfiguration(aPartnerConfiguration())
                .withLocale(Locale.getDefault())
                .build();
    }

    /**
     * Generate a valid {@link ContractConfiguration}.
     */
    public static ContractConfiguration aContractConfiguration() {
        Map<String, ContractProperty> contractProperties = new HashMap<>();
        contractProperties.put(Constants.ContractConfigurationKeys.KEY, new ContractProperty("Key"));
        contractProperties.put(Constants.ContractConfigurationKeys.MERCHANT_CODE, new ContractProperty("MerchantCode"));
        contractProperties.put(Constants.ContractConfigurationKeys.MERCHANT_TERMINAL, new ContractProperty("MerchantTerminal"));

        return new ContractConfiguration("Bizum", contractProperties);
    }
}
