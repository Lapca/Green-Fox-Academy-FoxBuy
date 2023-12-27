package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.ChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.DigitalProductChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.StripeChargeDTO;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface StripeService {
    StripeChargeDTO chargeVIP(ChargeDTO chargeDTO, BindingResult bindingResult);

    StripeChargeDTO charge(DigitalProductChargeDTO digitalProductChargeDTO, BindingResult bindingResult);

    Map<String, String> buildErrorJsonResponseForStripe(BindingResult bindingResult);

    Map<String, String> buildErrorJsonResponseForDigitalProduct(BindingResult bindingResult);
}
