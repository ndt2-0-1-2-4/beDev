package com.example.doan.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class MailData {
    private String emailToName;
    private String emailSubject;
    private String templateName;
    private Map<String, Object> model;
}
