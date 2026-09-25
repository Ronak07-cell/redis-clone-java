package redisclone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final Store store;

    public ClientHandler(Socket clientSocket, Store store) {
        this.clientSocket = clientSocket;
        this.store = store;
    }

    @Override
    public void run() {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String line;
            while ((line = in.readLine()) != null) {
                String response = handleCommand(line);
                out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    private String handleCommand(String line) {
        String[] parts = line.trim().split("\\s+");

        if (parts.length == 0 || parts[0].isEmpty()) {
            return "ERROR: empty command";
        }

        String command = parts[0].toUpperCase();

        switch (command) {
            case "SET":
                if (parts.length < 3) {
                    return "ERROR: SET requires a key and value";
                }
                store.set(parts[1], parts[2]);
                return "OK";

            case "GET":
                if (parts.length < 2) {
                    return "ERROR: GET requires a key";
                }
                String value = store.get(parts[1]);
                return value != null ? value : "(nil)";

            case "DEL":
                if (parts.length < 2) {
                    return "ERROR: DEL requires a key";
                }
                boolean deleted = store.delete(parts[1]);
                return deleted ? "1" : "0";

            case "EXISTS":
                if (parts.length < 2) {
                    return "ERROR: EXISTS requires a key";
                }
                boolean exists = store.exists(parts[1]);
                return exists ? "1" : "0";

            case "EXPIRE":
                if (parts.length < 3) {
                    return "ERROR: EXPIRE requires a key and seconds";
                }
                try {
                    long seconds = Long.parseLong(parts[2]);
                    boolean success = store.expire(parts[1], seconds);
                    return success ? "1" : "0";
                } catch (NumberFormatException e) {
                    return "ERROR: seconds must be a number";
                }

            default:
                return "ERROR: unknown command '" + command + "'";
        }
    }
}
