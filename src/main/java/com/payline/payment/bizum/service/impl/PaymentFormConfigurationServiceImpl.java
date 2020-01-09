package com.payline.payment.bizum.service.impl;


import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.service.PaymentFormConfigurationService;

import java.util.Locale;

public class PaymentFormConfigurationServiceImpl implements PaymentFormConfigurationService {


    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        return null;
    }

    @Override
    public PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {
        return null;
    }

    @Override
    public PaymentFormLogo getLogo(String s, Locale locale) {
        return null;
    }

    @Override
    public PaymentFormLogo getSchemeLogo(String schemeName) {
        return null;
    }
}
