package com.sonnvt.blog.utils;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Slf4j
public class Utils {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isEmptyFile(MultipartFile file) {
        return file == null || file.isEmpty();
    }

    public static String normalizeFileName(String fileName) {
        if (fileName == null) {
            return "default_file_name";
        }
        String normalized = fileName.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
        if (normalized.startsWith("_")) {
            normalized = "file" + normalized;
        }
        return normalized;
    }

    public static String timeFromNow(String time) {
        LocalDateTime now = LocalDateTime.now();
        if (time == null) {
            return "Unknown";
        } else if (time.contains(".")) {
            time = time.substring(0, time.indexOf("."));
        }
        LocalDateTime timeNormalize = parse(time);
        long diff = now.toEpochSecond(ZoneOffset.UTC) - timeNormalize.toEpochSecond(ZoneOffset.UTC);
        if (diff < 60) {
            return diff + " seconds ago";
        } else if (diff < 3600) {
            return diff / 60 + " minutes ago";
        } else if (diff < 86400) {
            return diff / 3600 + " hours ago";
        } else if (diff < 604800) {
            return diff / 86400 + " days ago";
        } else {
            return time;
        }
    }

    private static LocalDateTime parse(String time) {
        if (time.contains("T")) {
            return LocalDateTime.parse(time);
        } else {
            return LocalDateTime.parse(time, FORMATTER);
        }
    }

    public static Long cooldown(String time, Long durationInSeconds) {
        LocalDateTime now = LocalDateTime.now();
        if (time == null) {
            return null;
        } else if (time.contains(".")) {
            time = time.substring(0, time.indexOf("."));
        }
        LocalDateTime timeNormalize = parse(time);
        long diff = now.toEpochSecond(ZoneOffset.UTC) - timeNormalize.toEpochSecond(ZoneOffset.UTC);
        return (durationInSeconds - diff);
    }

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP"); // WebLogic
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
