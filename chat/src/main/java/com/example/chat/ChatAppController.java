package com.example.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Button;
import javafx.scene.control.TextInputDialog;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Controller
public class ChatAppController {

    @FXML
    private TextArea messageInput;

    @FXML
    private ListView<Contact> contactList;

    @FXML
    private ListView<String> messageList;

    @FXML
    private Button sendButton;

    private WebSocketClientHandler webSocketClientHandler;
    private final ObservableList<String> messages = FXCollections.observableArrayList();
    private final ObservableList<Contact> contacts = FXCollections.observableArrayList();

    private String username; 
    private Contact selectedContact;
    private Map<Contact, ObservableList<String>> contactMessages = new HashMap<>();

    public void initialize() {
        try {
            webSocketClientHandler = new WebSocketClientHandler();
            webSocketClientHandler.setController(this);
            webSocketClientHandler.connectToServer("ws://localhost:8080/chat"); 
            messageList.setItems(messages);

            setupListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUsername(String username) {
        this.username = username;

        if (webSocketClientHandler != null && username != null && !username.isEmpty()) {
            webSocketClientHandler.sendUsername(username);
            System.out.println("Username set to: " + username);
        } else {
            System.out.println("Failed to set username: WebSocket not connected or username is empty.");
        }
    }

    @FXML
    private void addContact() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Contact");
        dialog.setHeaderText("Enter contact name:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(name -> {
            Contact newContact = new Contact(name, "Online");
            contacts.add(newContact);
            contactMessages.put(newContact, FXCollections.observableArrayList());
            contactList.setItems(contacts);
        });
    }

    @FXML
    public void deleteContact() {
        Contact selectedContact = contactList.getSelectionModel().getSelectedItem();

        if (selectedContact != null) {
            contacts.remove(selectedContact);
            contactMessages.remove(selectedContact);
            contactList.setItems(contacts);
            messageList.getItems().clear(); // Clear the message list if the deleted contact is selected
        } else {
            System.out.println("No contact selected to delete.");
        }
    }

    @FXML
    private void sendMessage() {
        if (selectedContact != null) {
            String message = messageInput.getText();
            if (message != null && !message.isEmpty()) {
                String formattedMessage = username + " -> " + selectedContact.getName() + ": " + message;
                webSocketClientHandler.sendMessage(formattedMessage);

                ObservableList<String> contactChat = contactMessages.get(selectedContact);
                contactChat.add("You: " + message);
                messageInput.clear();
            }
        } else {
            System.out.println("No contact selected to send a message.");
        }
    }

    public void receiveMessage(String message) {
        String[] parts = message.split(": ", 2);
        if (parts.length == 2) {
            String senderName = parts[0];
            String content = parts[1];

            Contact sender = contacts.stream()
                    .filter(contact -> contact.getName().equals(senderName))
                    .findFirst()
                    .orElse(null);

            if (sender == null) {
                System.out.println("Sender not found: " + senderName);
                return;
            }

            ObservableList<String> senderChat = contactMessages.get(sender);
            if (senderChat == null) {
                senderChat = FXCollections.observableArrayList();
                contactMessages.put(sender, senderChat);
            }

            senderChat.add(senderName + ": " + content);

            if (sender.equals(selectedContact)) {
                messageList.setItems(senderChat);
            }
        } else {
            System.out.println("Invalid message format received.");
        }
    }

    private void setupListeners() {
        sendButton.setOnAction(event -> sendMessage());
        messageInput.setOnKeyPressed(event -> {
            if (event.getCode().getName().equals("Enter")) {
                event.consume();
                sendMessage();
            }
        });

        contactList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedContact = newValue;
                loadMessagesForContact(newValue);
            }
        });
    }

    private void loadMessagesForContact(Contact contact) {
        ObservableList<String> contactChat = contactMessages.get(contact);
        if (contactChat != null) {
            messageList.setItems(contactChat);
        } else {
            messageList.getItems().clear();
        }
    }
    
    private void updateContactList(String[] userStatusArray) {
        contacts.clear();
        for (String userStatus : userStatusArray) {
            String[] parts = userStatus.split("\\|");
            if (parts.length == 2) {
                String userName = parts[0];
                String status = parts[1];

                if (!userName.equals(username)) {
                    Contact contact = new Contact(userName, status);
                    contacts.add(contact);
                    contactMessages.putIfAbsent(contact, FXCollections.observableArrayList());
                }
            } else {
                System.out.println("Invalid user status format: " + userStatus);
            }
        }
        contactList.setItems(contacts);
    }

}
