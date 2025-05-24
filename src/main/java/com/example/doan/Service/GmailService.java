package com.example.doan.Service;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.example.doan.config.GmailClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GmailService {

    @Autowired
    private GmailClientConfig gmailClientConfig;

    private static final String OUTPUT_FOLDER = "email_exports";
    
    public void exportEmailsAsHtml() throws Exception {
        Gmail service = gmailClientConfig.getGmailService();
        createOutputFolder();
        
        List<Message> messages = service.users().messages()
                .list("me")
                .setLabelIds(Collections.singletonList("INBOX"))
                .setQ("is:unread")
                .execute()
                .getMessages();

        if (messages == null || messages.isEmpty()) {
            System.out.println("Không tìm thấy email nào trong hộp thư đến.");
            return;
        }

        System.out.println("Đang xuất " + messages.size() + " email dưới dạng HTML...");

        for (Message message : messages) {
            try {
                Message fullMessage = service.users().messages()
                        .get("me", message.getId())
                        .setFormat("full")  // Sử dụng full format
                        .execute();

                exportEmailAsHtmlFile(fullMessage);
            } catch (Exception e) {
                System.err.println("Lỗi khi xử lý email ID: " + message.getId());
                e.printStackTrace();
            }
        }
        
        System.out.println("Xuất email hoàn tất. Các file được lưu tại: " + 
                         Paths.get("").toAbsolutePath() + File.separator + OUTPUT_FOLDER);
    }

    private void createOutputFolder() throws IOException {
        Path path = Paths.get(OUTPUT_FOLDER);
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private void exportEmailAsHtmlFile(Message message) {
        try {
            String subject = getHeader(message, "Subject").orElse("Không có tiêu đề");
            String emailId = message.getId();
            String htmlContent = getFullHtmlContent(message);

            // Tạo tên file an toàn
            String safeSubject = subject.replaceAll("[^\\w\\s-]", "");
            String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());
            String filename = String.format("%s_%s_%s.html", 
                                         emailId.substring(0, Math.min(emailId.length(), 8)),
                                         timestamp,
                                         safeSubject.substring(0, Math.min(safeSubject.length(), 30)));

            // Ghi nội dung HTML vào file
            File file = new File(OUTPUT_FOLDER + File.separator + filename);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(htmlContent);
                System.out.println("Đã xuất: " + filename);
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi xuất email ID: " + message.getId());
            e.printStackTrace();
        }
    }

    private String getFullHtmlContent(Message message) {
        try {
            MessagePart payload = message.getPayload();
            if (payload == null) {
                return "<!-- Không có nội dung email -->";
            }

            // Nếu email chỉ có 1 phần
            if (payload.getParts() == null) {
                if ("text/html".equals(payload.getMimeType())) {
                    return decodeBase64(payload.getBody().getData());
                }
                return "<!-- Email không có nội dung HTML -->";
            }

            // Duyệt qua các phần của email
            for (MessagePart part : payload.getParts()) {
                if ("text/html".equals(part.getMimeType())) {
                    return decodeBase64(part.getBody().getData());
                }
                
                // Kiểm tra các phần con (cho email phức tạp)
                if (part.getParts() != null) {
                    for (MessagePart subPart : part.getParts()) {
                        if ("text/html".equals(subPart.getMimeType())) {
                            return decodeBase64(subPart.getBody().getData());
                        }
                    }
                }
            }
            
            return "<!-- Không tìm thấy nội dung HTML trong email -->";
        } catch (Exception e) {
            return "<!-- Lỗi khi trích xuất HTML: " + e.getMessage() + " -->";
        }
    }

    private Optional<String> getHeader(Message message, String name) {
        try {
            if (message.getPayload() == null || message.getPayload().getHeaders() == null) {
                return Optional.empty();
            }

            for (MessagePartHeader header : message.getPayload().getHeaders()) {
                if (header.getName().equalsIgnoreCase(name)) {
                    return Optional.ofNullable(header.getValue());
                }
            }
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String decodeBase64(String data) {
        if (data == null) return "";
        try {
            byte[] decodedBytes = Base64.getUrlDecoder().decode(data);
            return new String(decodedBytes);
        } catch (Exception e) {
            return "";
        }
    }
}