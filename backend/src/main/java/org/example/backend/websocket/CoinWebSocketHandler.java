package org.example.backend.websocket;

import org.example.backend.protobuf.CoinProto;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;

@Component
public class CoinWebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, Set<String>> subscriptions =
            new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {

        subscriptions.put(session, new HashSet<>());

        System.out.println("NEW WS SESSION");
    }

    @Override
    protected void handleTextMessage(
            WebSocketSession session,
            TextMessage message
    ) {

        String coin = message.getPayload().toUpperCase();

        if (!coin.endsWith("USDT")) {
            coin += "USDT";
        }

        subscriptions.get(session).add(coin);

        System.out.println("SUBSCRIBED: " + coin);
    }

    @Override
    public void afterConnectionClosed(
            WebSocketSession session,
            CloseStatus status
    ) {

        subscriptions.remove(session);

        System.out.println("SESSION CLOSED");
    }

    public void broadcast(String symbol, double price) {

        try {

            CoinProto.CoinUpdate update =
                    CoinProto.CoinUpdate.newBuilder()
                            .setSymbol(symbol)
                            .setPrice(price)
                            .build();

            byte[] bytes = update.toByteArray();

            subscriptions.forEach((session, coins) -> {

                try {

                    if (
                            session.isOpen()
                                    && coins.contains(symbol)
                    ) {

                        session.sendMessage(
                                new BinaryMessage(bytes)
                        );

                        System.out.println(
                                "SEND TO CLIENT: "
                                        + symbol
                                        + " "
                                        + price
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}