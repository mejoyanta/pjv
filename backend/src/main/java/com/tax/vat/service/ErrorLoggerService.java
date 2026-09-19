package com.tax.vat.service;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ErrorLoggerService {

    private static final Logger log = LoggerFactory.getLogger(ErrorLoggerService.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    private final Path logFilePath;

    public ErrorLoggerService() {
        // Log directory relative to backend root: logs/error.log
        Path logDir = Paths.get("logs");
        try {
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
            }
        } catch (IOException e) {
            log.error("Failed to create log directory: {}", logDir.toAbsolutePath(), e);
        }
        this.logFilePath = logDir.resolve("error.log");
    }

    public synchronized void logError(HttpServletRequest request, HttpStatus status, String customMessage, Throwable ex) {
        try {
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            
            // Frontend Page URL & Backend API Method/URL
            String method = request != null ? request.getMethod() : "UNKNOWN";
            String fullUrl = request != null ? getFullRequestUrl(request) : "N/A";
            String clientIp = request != null ? getClientIp(request) : "N/A";
            String pageUrl = extractFrontendPageUrl(request);

            // Root Cause Discovery
            Throwable rootCause = ex;
            if (rootCause != null) {
                while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
                    rootCause = rootCause.getCause();
                }
            }

            String exName = ex != null ? ex.getClass().getName() : "UnknownException";
            String exMessage = ex != null ? ex.getMessage() : customMessage;
            String rootCauseDetail = rootCause != null 
                    ? rootCause.getClass().getName() + ": " + rootCause.getMessage()
                    : "None";

            StringBuilder sb = new StringBuilder();
            sb.append("================================================================================\n");
            sb.append("[").append(timestamp).append("] ERROR\n");
            sb.append("PAGE URL:    ").append(pageUrl).append("\n");
            sb.append("API URL:     ").append(method).append(" ").append(fullUrl).append("\n");
            sb.append("Client IP:   ").append(clientIp).append("\n");
            sb.append("Status:      ").append(status != null ? status.value() + " " + status.getReasonPhrase() : "N/A").append("\n");
            sb.append("Message:     ").append(customMessage != null ? customMessage : exMessage).append("\n");
            sb.append("Exception:   ").append(exName).append(": ").append(exMessage).append("\n");
            sb.append("Root Cause:  ").append(rootCauseDetail).append("\n");
            
            if (ex != null) {
                sb.append("Stack Trace Snippet:\n");
                StackTraceElement[] trace = ex.getStackTrace();
                int count = 0;
                for (StackTraceElement elem : trace) {
                    sb.append("    at ").append(elem.toString()).append("\n");
                    count++;
                    if (count >= 15) {
                        sb.append("    ... and ").append(trace.length - count).append(" more lines\n");
                        break;
                    }
                }
            }
            sb.append("================================================================================\n\n");

            // Write to file (UTF-8, Append)
            Files.writeString(
                    logFilePath,
                    sb.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

            log.error("Error on page [{}] - API [{} {}] - {}", pageUrl, method, fullUrl, exMessage);

        } catch (Exception e) {
            log.error("Failed to write error to log file", e);
        }
    }

    public synchronized void logClientError(String pageUrl, String errorMsg, String componentStack, String userAgent) {
        try {
            String timestamp = LocalDateTime.now().format(DATE_TIME_FORMATTER);
            StringBuilder sb = new StringBuilder();
            sb.append("================================================================================\n");
            sb.append("[").append(timestamp).append("] FRONTEND / CLIENT ERROR\n");
            sb.append("PAGE URL:    ").append(pageUrl != null && !pageUrl.isBlank() ? pageUrl : "N/A").append("\n");
            sb.append("Message:     ").append(errorMsg).append("\n");
            if (userAgent != null && !userAgent.isBlank()) {
                sb.append("User-Agent:  ").append(userAgent).append("\n");
            }
            if (componentStack != null && !componentStack.isBlank()) {
                sb.append("Component Stack:\n").append(componentStack).append("\n");
            }
            sb.append("================================================================================\n\n");

            Files.writeString(
                    logFilePath,
                    sb.toString(),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
            log.error("Client UI Error on page [{}]: {}", pageUrl, errorMsg);
        } catch (Exception e) {
            log.error("Failed to write client error to log file", e);
        }
    }

    public Path getLogFilePath() {
        return logFilePath.toAbsolutePath();
    }

    public synchronized String readRecentLogs(int maxLines) {
        if (!Files.exists(logFilePath)) {
            return "No error logs recorded yet. (File: " + logFilePath.toAbsolutePath() + ")";
        }
        try {
            List<String> lines = Files.readAllLines(logFilePath, StandardCharsets.UTF_8);
            if (lines.isEmpty()) {
                return "Error log file is empty.";
            }
            int start = Math.max(0, lines.size() - maxLines);
            List<String> subList = lines.subList(start, lines.size());
            return String.join("\n", subList);
        } catch (IOException e) {
            return "Error reading log file: " + e.getMessage();
        }
    }

    public synchronized boolean clearLogs() {
        try {
            if (Files.exists(logFilePath)) {
                Files.writeString(logFilePath, "", StandardCharsets.UTF_8, StandardOpenOption.TRUNCATE_EXISTING);
            }
            return true;
        } catch (IOException e) {
            log.error("Failed to clear log file", e);
            return false;
        }
    }

    private String extractFrontendPageUrl(HttpServletRequest request) {
        if (request == null) return "N/A";
        String xPageUrl = request.getHeader("X-Page-Url");
        if (xPageUrl != null && !xPageUrl.isBlank()) {
            return xPageUrl.trim();
        }
        String referer = request.getHeader("Referer");
        if (referer != null && !referer.isBlank()) {
            return referer.trim();
        }
        return "N/A";
    }

    private String getFullRequestUrl(HttpServletRequest request) {
        StringBuffer requestURL = request.getRequestURL();
        String queryString = request.getQueryString();
        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
