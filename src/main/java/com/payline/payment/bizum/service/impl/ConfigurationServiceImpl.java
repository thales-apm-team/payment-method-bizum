package com.payline.payment.bizum.service.impl;


import com.payline.payment.bizum.utils.Constants;
import com.payline.payment.bizum.utils.http.BizumHttpClient;
import com.payline.payment.bizum.utils.i18n.I18nService;
import com.payline.payment.bizum.utils.properties.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.PasswordParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.service.ConfigurationService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;




public class ConfigurationServiceImpl implements ConfigurationService {

    private I18nService i18n = I18nService.getInstance();
    private ReleaseProperties releaseProperties = ReleaseProperties.getInstance();
    private BizumHttpClient byzumHttpClient = BizumHttpClient.getInstance();

    /**------------------------------------------------------------------------------------------------------------------*/
    @java.lang.Override
    public java.util.List<AbstractParameter> getParameters(java.util.Locale locale) {

        List<AbstractParameter> parameters = new ArrayList<>();

        // KEY
        PasswordParameter key = new PasswordParameter();
        key.setKey( Constants.ContractConfigurationKeys.KEY);
        key.setLabel( i18n.getMessage("contract.KEY.label", locale) );
        key.setDescription( i18n.getMessage("contract.KEY.description", locale) );
        key.setRequired( true );
        parameters.add( key );

        // MERCHANT CODE
        InputParameter merchantCode = new InputParameter();
        merchantCode.setKey( Constants.ContractConfigurationKeys.MERCHANT_CODE);
        merchantCode.setLabel( i18n.getMessage("contract.MERCHANT_CODE.label", locale) );
        merchantCode.setDescription( i18n.getMessage("contract.MERCHANT_CODE.description", locale) );
        merchantCode.setRequired( true );
        parameters.add(merchantCode);

        // MERCHANT TERMINAL
        InputParameter merchantTerminal = new InputParameter();
        merchantTerminal.setKey( Constants.ContractConfigurationKeys.MERCHANT_TERMINAL);
        merchantTerminal.setLabel( i18n.getMessage("contract.MERCHANT_TERMINAL.label", locale) );
        merchantTerminal.setDescription( i18n.getMessage("contract.MERCHANT_TERMINAL.description", locale) );
        merchantTerminal.setRequired( true );
        parameters.add(merchantTerminal);

        return parameters;
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @java.lang.Override
    public java.util.Map<java.lang.String, java.lang.String> check(ContractParametersCheckRequest contractParametersCheckRequest) {

        final Map<String, String> errors = new HashMap<>();

        Map<String, String> accountInfo = contractParametersCheckRequest.getAccountInfo();

        Locale locale = contractParametersCheckRequest.getLocale();

        // check required fields
        for( AbstractParameter param : this.getParameters( locale ) ){
            if( param.isRequired() && accountInfo.get( param.getKey() ) == null ){
                String message = i18n.getMessage("contract." + param.getKey() + ".requiredError", locale);
                errors.put( param.getKey(), message );
            }
        }

        // If merchant key is missing, no need to go further, as it is required
        String merchantKey = Constants.ContractConfigurationKeys.KEY;

        if( errors.containsKey(merchantKey)){
            return errors;
        }

        // TODO : Vérifier la KEY du merchant

        return null;
    }

    /**------------------------------------------------------------------------------------------------------------------*/
    @Override
    public ReleaseInformation getReleaseInformation() {
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate( LocalDate.parse(releaseProperties.get("release.date"), DateTimeFormatter.ofPattern("dd/MM/yyyy")) )
                .withVersion( releaseProperties.get("release.version") )
                .build();
    }
    /**------------------------------------------------------------------------------------------------------------------*/
    @Override
    public String getName(Locale locale) {
        return i18n.getMessage("paymentMethod.name", locale);
    }
}
