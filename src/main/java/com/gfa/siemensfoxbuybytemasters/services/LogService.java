package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.LogDTO;
import com.gfa.siemensfoxbuybytemasters.models.Log;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface LogService {

    List<LogDTO> getLogsByDate(LocalDate date);

    List<LogDTO> convertLogsToLogDTOs(List<Log> logs);

    void logRequest(String endpoint, boolean success);
}
