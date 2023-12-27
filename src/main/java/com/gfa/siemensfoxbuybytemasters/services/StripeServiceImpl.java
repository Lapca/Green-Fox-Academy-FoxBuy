package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.ChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.DigitalProductChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.StripeChargeDTO;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.HashMap;
import java.util.Map;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.apiKey}")
    private String stripeApiKey;
    @Value("${vip.account.price}")
    private String vipPrice;


    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public StripeChargeDTO chargeVIP(ChargeDTO chargeDTO, BindingResult bindingResult) {
        StripeChargeDTO stripeChargeDTO = new StripeChargeDTO();
        try {
            stripeChargeDTO.setSuccess(false);
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", (Integer.parseInt(vipPrice) * 100));
            chargeParams.put("currency", chargeDTO.getCurrency());
            chargeParams.put("description", "Payment for id " + stripeChargeDTO.getAdditionalInfo()
                    .getOrDefault("ID_TAG", ""));
            chargeParams.put("source", chargeDTO.getCardToken());

            Map<String, Object> metaData = new HashMap<>();
            metaData.put("id", stripeChargeDTO.getChargeId());
            metaData.putAll(stripeChargeDTO.getAdditionalInfo());

            chargeParams.put("metadata", metaData);
            Charge charge = Charge.create(chargeParams);
            stripeChargeDTO.setMessage(charge.getOutcome().getSellerMessage());
            stripeChargeDTO.setAmount((double) charge.getAmount());
            if (charge.getPaid()) {
                stripeChargeDTO.setChargeId(charge.getId());
                stripeChargeDTO.setSuccess(true);
            }
            return stripeChargeDTO;
        } catch (StripeException e) {
            bindingResult.addError(new FieldError("ChargeDTO", "cardToken",
                    e.getStripeError().getMessage()));
            return stripeChargeDTO;
        }

    }

    @Override
    public StripeChargeDTO charge(DigitalProductChargeDTO digitalProductChargeDTO, BindingResult bindingResult) {
        StripeChargeDTO stripeChargeDTO = new StripeChargeDTO();
        try {
            stripeChargeDTO.setSuccess(false);
            Map<String, Object> chargeParams = new HashMap<>();
            chargeParams.put("amount", digitalProductChargeDTO.getAmount());
            chargeParams.put("currency", digitalProductChargeDTO.getCurrency());
            chargeParams.put("description", "Payment for id " + stripeChargeDTO.getAdditionalInfo()
                    .getOrDefault("ID_TAG", ""));
            chargeParams.put("source", digitalProductChargeDTO.getCardToken());

            Map<String, Object> metaData = new HashMap<>();
            metaData.put("id", stripeChargeDTO.getChargeId());
            metaData.putAll(stripeChargeDTO.getAdditionalInfo());

            chargeParams.put("metadata", metaData);
            Charge charge = Charge.create(chargeParams);
            stripeChargeDTO.setMessage(charge.getOutcome().getSellerMessage());
            stripeChargeDTO.setAmount((double) charge.getAmount());
            if (charge.getPaid()) {
                stripeChargeDTO.setChargeId(charge.getId());
                stripeChargeDTO.setSuccess(true);
            }
            return stripeChargeDTO;
        } catch (StripeException e) {
            bindingResult.addError(new FieldError("ChargeDTO", "cardToken",
                    e.getStripeError().getMessage()));
            return stripeChargeDTO;
        }
    }

    @Override
    public Map<String, String> buildErrorJsonResponseForStripe(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            if (fieldError.getField().equals("addressCountry")) {
                errors.put("errorCountry", "Country field can not be empty!");
            }
            if (fieldError.getField().equals("addressZipcode")) {
                errors.put("errorZipcode", "Zipcode  field can not be empty!");
            }
            if (fieldError.getField().equals("addressCity")) {
                errors.put("errorCity", "City field can not be empty");
            }
            if (fieldError.getField().equals("addressLine")) {
                errors.put("errorLine", "Address line can not be empty!");
            }
        }
        return errors;
    }

    @Override
    public Map<String, String> buildErrorJsonResponseForDigitalProduct(BindingResult bindingResult) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {

            if (fieldError.getField().equals("currency")) {
                errors.put("errorCurrency", "Currency field can not be empty!");
            }
        }
        return errors;
    }

}

