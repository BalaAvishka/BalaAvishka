import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.net.http.WebSocket.Listener;
import java.util.Scanner;
import java.util.concurrent.CompletionStage;

public class SimpleChatClient {
    public static void main(String[] args) {
        String wsEndpoint = "wss://ws.ifelse.io"; // ✅ Working public echo WebSocket

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String sender = scanner.nextLine();

        HttpClient client = HttpClient.newHttpClient();
        WebSocket webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(wsEndpoint), new ChatWebSocketListener(sender))
                .join();

        System.out.println("Connected to echo chat!\nType messages (type 'exit' to quit):");

        while (true) {
            String message = scanner.nextLine();
            if (message.equalsIgnoreCase("exit")) break;

            // Send plain message (not JSON)
            webSocket.sendText(sender + ": " + message, true);
        }

        webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "Bye").thenRun(() -> System.out.println("Disconnected."));
        scanner.close();
    }

    private static class ChatWebSocketListener implements Listener {
        private final String sender;

        ChatWebSocketListener(String sender) {
            this.sender = sender;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            System.out.println("WebSocket connection opened.");
            Listener.super.onOpen(webSocket);
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            System.out.println("Received: " + data.toString());
            return Listener.super.onText(webSocket, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            System.out.println("WebSocket closed: " + reason);
            return Listener.super.onClose(webSocket, statusCode, reason);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            System.out.println("Error: " + error.getMessage());
        }
    }
}
