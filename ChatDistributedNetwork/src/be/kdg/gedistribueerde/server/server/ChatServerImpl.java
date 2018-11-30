package be.kdg.gedistribueerde.server.server;

import be.kdg.gedistribueerde.server.model.ChatPerson;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the ChatServer component.
 */
public final class ChatServerImpl implements ChatServer {

    /**
     * Acts as a persistent store of clients that are currently online
     * Could be replaced with an underlying DB in the future
     */
    private List<ChatPerson> clients;
    private ChatClient chatClient;

    public ChatServerImpl() {
        this.clients = new ArrayList<ChatPerson>();
        this.chatClient = new ChatClientStub(null);
    }

    @Override
    public void register(ChatPerson chatPerson) {
        System.out.println("BEGIN: CHATSERVERSIMPL: register " + chatPerson.toString());
        clients.add(chatPerson);
        send("server", chatPerson.getName() + " has entered the room");
        System.out.println("END: CHATSERVERSIMPL: register " + chatPerson.toString());
    }

    @Override
    public void unRegister(ChatPerson chatPerson) {
        clients.remove(chatPerson);
        send("server", chatPerson.getName() + " has left the room");
    }

    @Override
    public void send(String name, String message) {
        System.out.println("BEGIN: CHATSERVERSIMPL: send " + name + " " + message);
        for(ChatPerson client : clients) {
            ((ChatClientStub) chatClient).setChatClientAddress(client.getNetworkAddress());
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    chatClient.receive(name + ": " + message);
                }
            };
            runnable.run();
        }
        System.out.println("END: CHATSERVERSIMPL: send " + name + " " + message);
    }
}
