package com.example.doan.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.doan.Service.GmailService;

@RestController
@RequestMapping("/api/v1/test")
public class TestApi {

    @Autowired 
    private GmailService gmailService;

    @GetMapping("/newmail")
    public ResponseEntity<?> CheckNewMail() {
        try {
            gmailService.exportEmailsAsHtml();
            return ResponseEntity.ok("Success");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error");
        }
        
    }
    
}
