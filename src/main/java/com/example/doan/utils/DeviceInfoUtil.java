package com.example.doan.utils;

import jakarta.servlet.http.HttpServletRequest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeviceInfoUtil {

    public static String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        } else {
            ip = ip.split(",")[0];
        }
        return ip;
    }

    public static String getBrowserInfo(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null)
            return "Unknown";
        if (userAgent.contains("Chrome"))
            return "Chrome";
        if (userAgent.contains("Firefox"))
            return "Firefox";
        if (userAgent.contains("Safari") && !userAgent.contains("Chrome"))
            return "Safari";
        if (userAgent.contains("Edge"))
            return "Edge";
        if (userAgent.contains("Opera") || userAgent.contains("OPR"))
            return "Opera";
        return "Other";
    }

    public static String getDeviceType(HttpServletRequest request) {
        String userAgent = request.getHeader("User-Agent");
        if (userAgent == null)
            return "Unknown";
        if (userAgent.contains("Mobile") || userAgent.contains("Android") || userAgent.contains("iPhone")) {
            return "Mobile";
        }
        if (userAgent.contains("Tablet"))
            return "Tablet";
        return "Desktop";
    }

    public static String extractDetail(String userAgent, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userAgent);
        return matcher.find() ? matcher.group(0) : "Unknown";
    }
}
