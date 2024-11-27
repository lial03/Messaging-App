package com.example.chat;

import jakarta.websocket.*;
import java.net.URI;

@ClientEndpoint
public class WebSocketClientHandler {

    private Session session;
    private ChatAppController controller;

    public void connectToServer(String uri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            container.connectToServer(this, new URI(uri));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("Connected to the server");
    }

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);

        // Notify the controller about the received message
        javafx.application.Platform.runLater(() -> {
            if (controller != null) {
                controller.receiveMessage(message);
            }
        });
    }

    public void sendUsername(String username) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(username);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            if (session != null && session.isOpen()) {
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        this.session = null;
        System.out.println("Disconnected from server: " + reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("WebSocket error occurred: " + throwable.getMessage());
        throwable.printStackTrace();
    }

    public void setController(ChatAppController controller) {
        this.controller = controller;
    }
}
