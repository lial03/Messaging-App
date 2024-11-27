package com.example.ChatApp;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/messages")
public class MessageController {
    private List<Message> messages = new ArrayList<>();

    @GetMapping("/history")
    public List<Message> getMessages() {
        return messages;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody Message message) {
        messages.add(message);
        System.out.println("Message sent: " + message); 
        return ResponseEntity.ok("Message sent");
    }

}
class Message {
    @NotBlank(message = "Sender cannot be empty")
    private String sender;

    @NotBlank(message = "Receiver cannot be empty")
    private String receiver;

    @NotBlank(message = "Content cannot be empty")
    private String content;

    public Message(String sender, String receiver, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}

