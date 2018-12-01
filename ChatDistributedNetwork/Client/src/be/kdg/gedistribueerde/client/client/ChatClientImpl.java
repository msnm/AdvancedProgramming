package be.kdg.gedistribueerde.client.client;


import be.kdg.gedistribueerde.client.communication.NetworkAddress;
import be.kdg.gedistribueerde.client.model.ChatPerson;
import be.kdg.gedistribueerde.server.server.ChatServer;

public final class ChatClientImpl  implements ChatClient  {
    private ChatServer chatServer;
    private TextReceiver textReceiver;
    private String name;
    private NetworkAddress clientSkeletonAddress;

    public ChatClientImpl(String name, ChatServer chatServer, NetworkAddress clientSkeletonAddress) {
        this.chatServer = chatServer;
        this.name = name;
        this.clientSkeletonAddress = clientSkeletonAddress;
    }

    public String getName() {
        return name;
    }

    public void send(String message) {
        chatServer.send(name, message);
    }

    public void unregister() {

        ChatPerson chatPerson = new ChatPerson(this.getName());
        chatPerson.setNetworkAddress(clientSkeletonAddress);
        chatServer.unRegister(chatPerson);
    }

    public void register() {
        System.out.println("BEGIN: CHATCLIENTIMPL: registering person " + this.getName());
        ChatPerson chatPerson = new ChatPerson(this.getName());
        chatPerson.setNetworkAddress(clientSkeletonAddress);
        chatServer.register(chatPerson);
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
