package be.kdg.gedistribueerde.server.server;

import be.kdg.gedistribueerde.server.communication.NetworkAddress;
import be.kdg.gedistribueerde.server.model.ChatPerson;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of the ChatServer component.
 */
public final class ChatServerImpl implements ChatServer {

    /**
     * Acts as a persistent store of clients that are currently online
     */
    private List<ChatClient> clients;
;
    public ChatServerImpl() {
        this.clients = new ArrayList<>();
    }

    @Override
    public void register(ChatClient chatClient) {
        System.out.println("BEGIN: CHATSERVERSIMPL");
        String name = chatClient.getName();
        System.out.println("BEGIN: CHATSERVERSIMPL: register " + name);
        clients.add(chatClient);
        send("server", name + " has entered the room");
        System.out.println("END: CHATSERVERSIMPL: register " + name);
    }

    @Override
    public void unRegister(ChatClient chatClient) {
        System.out.println("BEGIN: unREGISTER. Number of users before " + clients.size() + " contains" + clients.contains(chatClient));
        String name = chatClient.getName();
        clients.remove(chatClient);
        System.out.println("BEGIN: unREGISTER. Number of users after " + clients.size() + " contains" + clients.contains(chatClient));
        send("server", name + " has left the room");
    }

    @Override
    public void send(String name, String message) {
        System.out.println("BEGIN: CHATSERVERSIMPL: send to "+  clients.size() + name + " " + message);
        String messageToSend = name.concat(": ").concat(message);
        clients.stream().forEach(v -> v.receive(messageToSend));
        System.out.println("END: CHATSERVERSIMPL: send " + name + " " + message);
    }
}
