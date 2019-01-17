package be.kdg.gedistribueerde.client.client;


import be.kdg.gedistribueerde.server.server.ChatServer;

public final class ChatClientImpl implements ChatClient  {
    private ChatServer chatServer;
    private TextReceiver textReceiver;
    private String name;


    public ChatClientImpl(ChatServer chatServer, String name) {
        this.chatServer = chatServer;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void send(String message) {
        chatServer.send(this.name, message);
    }

    public void unregister() {
        chatServer.unRegister(this);
    }

    public void register() {
        System.out.println("BEGIN: CHATCLIENTIMPL: registering person " + this.getName());
        chatServer.register(this);
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
