package be.kdg.gedistribueerde.client;

import be.kdg.gedistribueerde.client.client.ChatClient;
import be.kdg.gedistribueerde.client.client.ChatClientImpl;
import be.kdg.gedistribueerde.client.client.ChatClientSkeleton;
import be.kdg.gedistribueerde.client.client.ChatServerStub;
import be.kdg.gedistribueerde.client.communication.MessageManager;
import be.kdg.gedistribueerde.client.communication.NetworkAddress;
import be.kdg.gedistribueerde.client.ui.ChatFrame;
import be.kdg.gedistribueerde.server.server.ChatServer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class StartClient2 {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Client <contactsIP> <contactsPort>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[1]);

        NetworkAddress chatServerAddress = new NetworkAddress(args[0], port);

        MessageManager clientsOnThisMachine = new MessageManager();
        ChatServer chatServer = new ChatServerStub(chatServerAddress, clientsOnThisMachine);

        ChatClient chatClient1 = new ChatClientImpl("Joske", chatServer);
        new ChatFrame(chatClient1);
        ChatClient chatClient2 = new ChatClientImpl("Maria", chatServer);
        new ChatFrame(chatClient2);

        List<ChatClient> chatClients = new ArrayList<>();
        chatClients.add(chatClient1);
        chatClients.add(chatClient2);

        ChatClientSkeleton chatClientSkeleton1 = new ChatClientSkeleton(chatClients, clientsOnThisMachine);

        Thread thread = new Thread(chatClientSkeleton1);

        //This is a Deamon thread, because it is a non-blocking thread. This means if the JVM stops, this thread
        //will not prevent it. A deamon thread is like a background service.
        thread.setDaemon(true);
        thread.start();

        ((ChatClientImpl) chatClient1).register();
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ((ChatClientImpl) chatClient2).register();

    }
}
