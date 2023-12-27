package com.gfa.siemensfoxbuybytemasters.services;

import com.gfa.siemensfoxbuybytemasters.models.DTOs.LogDTO;
import com.gfa.siemensfoxbuybytemasters.models.Log;
import com.gfa.siemensfoxbuybytemasters.repositories.LogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LogServiceImp implements LogService{

    private final LogRepository logRepository;

    @Autowired
    public LogServiceImp(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    @Override
    public List<LogDTO> getLogsByDate(LocalDate date) {
        List<Log> logs = logRepository.findByDateAfter(date);

        return convertLogsToLogDTOs(logs);
    }

    @Override
    public List<LogDTO> convertLogsToLogDTOs(List<Log> logs) {

        return logs.stream()
                .map(log -> new LogDTO(
                        log.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                        log.getEndpoint(),
                        log.getType(),
                        log.getData()))
                .collect(Collectors.toList());
    }

    @Override
    public void logRequest(String endpoint, boolean success) {
        String logType = success ? "INFO" : "ERROR";
        String logData = success ? "Request was successful." : "Request failed.";

        Log log = new Log(endpoint, logType, logData);
        logRepository.save(log);
    }

}
