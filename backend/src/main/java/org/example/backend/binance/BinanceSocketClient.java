package org.example.backend.binance;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.example.backend.websocket.CoinWebSocketHandler;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BinanceSocketClient {

    private final CoinWebSocketHandler handler;


    public BinanceSocketClient(
            CoinWebSocketHandler handler
    ) {

        this.handler = handler;
    }

    @PostConstruct
    public void start() throws Exception {

        List<String> allCoins = new ArrayList<>(List.of(
                "btcusdt",
                "ethusdt",
                "bnbusdt",
                "solusdt"
        ));

        Collections.shuffle(allCoins);

        List<String> selected = allCoins.subList(0, 3);

        String stream = selected.stream()
                .map(s -> s + "@trade")
                .reduce((a, b) -> a + "/" + b)
                .orElse("");

        String url =
                "wss://stream.binance.com:9443/stream?streams=" + stream;
        WebSocketClient client =
                new WebSocketClient(new URI(url)) {

                    @Override
                    public void onOpen(ServerHandshake handshakedata) {
                        System.out.println("BINANCE CONNECTED: " + stream);
                    }

                    @Override
                    public void onMessage(String message) {

                        try {

                            ObjectMapper mapper = new ObjectMapper();

                            JsonNode root = mapper.readTree(message);
                            JsonNode data = root.get("data");

                            String symbol = data.get("s").asText().toUpperCase();
                            double price = data.get("p").asDouble();

                            if (!symbol.endsWith("USDT")) return;

                            handler.broadcast(symbol, price);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        System.out.println("BINANCE CLOSED");
                    }

                    @Override
                    public void onError(Exception ex) {
                        ex.printStackTrace();
                    }
                };

        client.connect();

        System.out.println("BINANCE STARTED");
    }
}