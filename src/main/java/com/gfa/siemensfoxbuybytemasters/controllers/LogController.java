package com.gfa.siemensfoxbuybytemasters.controllers;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.ErrorDTO;
import com.gfa.siemensfoxbuybytemasters.models.DTOs.LogDTO;
import com.gfa.siemensfoxbuybytemasters.models.Log;
import com.gfa.siemensfoxbuybytemasters.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs")
    public ResponseEntity<?> getLogsByDate(@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        if(Optional.ofNullable(date).isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorDTO("Date parameter is missing, please provide a valid date!"));
        }
        List<LogDTO> logs = logService.getLogsByDate(date);

        if(logs.isEmpty()) {
            return ResponseEntity.badRequest().body(new ErrorDTO("No logs found for the given date!"));
        }
        return ResponseEntity.ok().body(logs);
    }


}
