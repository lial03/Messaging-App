package com.example.ChatApp;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<WebSocketSession, String> userSessions = new HashMap<>();
    private static final Set<String> onlineUsers = new HashSet<>();
    private static final List<WebSocketSession> sessions = new ArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        session.sendMessage(new TextMessage("Hello there!"));
        System.out.println("New connection established");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        if (!userSessions.containsKey(session)) {
            userSessions.put(session, payload);
            onlineUsers.add(payload);

            broadcastUserList();
        } else {
            String sender = userSessions.get(session);
            for (WebSocketSession s : sessions) {
                if (s.isOpen()) {
                    s.sendMessage(new TextMessage(sender + ": " + payload));
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = userSessions.remove(session);
        if (username != null) {
            onlineUsers.remove(username);
            sessions.remove(session);
            broadcastUserList();
        }
        System.out.println("Connection closed");
    }

    private void broadcastUserList() throws Exception {
        String userListMessage = "USER_LIST:" + String.join(",", onlineUsers);
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(userListMessage));
            }
        }
    }
}
