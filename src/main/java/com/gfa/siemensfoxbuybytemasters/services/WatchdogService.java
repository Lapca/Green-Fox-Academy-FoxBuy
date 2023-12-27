package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.AdDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.WatchdogDTO;
import com.gfa.siemensfoxbuybytemasters.models.Watchdog;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;

import java.util.Map;

public interface WatchdogService {

    Map<String, String> buildErrorJsonResponseforWatchdog(BindingResult bindingResult);

    Watchdog saveWatchdog(Watchdog watchdog);

    ResponseEntity<?> setUpWatchdog(WatchdogDTO watchdogDTO, UserDetails userDetails);

    void checkWatchdogs(AdDTO adDTO);

}
