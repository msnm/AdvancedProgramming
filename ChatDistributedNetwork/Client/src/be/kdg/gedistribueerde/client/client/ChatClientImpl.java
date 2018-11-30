package be.kdg.gedistribueerde.client.client;


import be.kdg.gedistribueerde.client.model.ChatPerson;
import be.kdg.gedistribueerde.server.server.ChatServer;

public final class ChatClientImpl  implements ChatClient  {
    private ChatServer chatServer;
    private TextReceiver textReceiver;
    private String name;

    public ChatClientImpl(String name, ChatServer chatServer) {
        this.chatServer = chatServer;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void send(String message) {
        chatServer.send(name, message);
    }

    public void unregister() {
        chatServer.unRegister(new ChatPerson(this.getName()));
    }

    public void register() {
        System.out.println("BEGIN: CHATCLIENTIMPL: registering person " + this.getName());
        chatServer.register(new ChatPerson(this.getName()));
        System.out.println("END: CHATCLIENTIMPL: registering person " + this.getName());
    }

    @Override
    public void receive(String message) {
        if (textReceiver == null) {
            System.out.println(message);
            return;
        }
        textReceiver.receive(message);
    }

    public void setTextReceiver(TextReceiver textReceiver) {
        this.textReceiver = textReceiver;
    }

}
