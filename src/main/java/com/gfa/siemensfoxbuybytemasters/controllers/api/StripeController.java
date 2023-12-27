package com.gfa.siemensfoxbuybytemasters.controllers.api;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.ChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.StripeChargeDTO;
import com.gfa.siemensfoxbuybytemasters.models.User;
import com.gfa.siemensfoxbuybytemasters.services.StripeService;
import com.gfa.siemensfoxbuybytemasters.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;



@RestController
public class StripeController {

    public final StripeService stripeService;
    public final UserService userService;

    @Autowired
    public StripeController(StripeService stripeService, UserService userService) {
        this.stripeService = stripeService;
        this.userService = userService;
    }


    @PostMapping("/vip")
    @ResponseBody
    public ResponseEntity<?> charge(@Valid @RequestBody ChargeDTO chargeDTO, BindingResult bindingResult, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername()).get();
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(stripeService.buildErrorJsonResponseForStripe(bindingResult));
        }
        StripeChargeDTO stripeChargeDTO = stripeService.chargeVIP(chargeDTO, bindingResult);
        //call invoice service here using data from chargeDTO

        return userService.vipPaymentResponse(user, stripeChargeDTO);
    }


}
