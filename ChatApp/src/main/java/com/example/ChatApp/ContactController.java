package com.example.ChatApp;

import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/check")
public class ContactController {
    private final List<Contact> contacts = new ArrayList<>();

    @GetMapping("/contacts")
    public List<Contact> getAllContacts() {
        return contacts;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addContact(@RequestBody Contact contact) {
        contacts.add(contact);
        return new ResponseEntity<>("Contact added", HttpStatus.CREATED);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<String> deleteContact(@PathVariable String name) {
        boolean removed = contacts.removeIf(contact -> contact.getName().equals(name));

        if (removed) {
            return new ResponseEntity<>("Contact removed", HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Contact not found", HttpStatus.NOT_FOUND);
        }
    }
}
class Contact {

    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotBlank(message = "Status cannot be empty")
    private String status;

    public Contact() {
    }

    public Contact(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
