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
import java.util.Scanner;

/**
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class StartClient {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Client <contactsIP> <contactsPort>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[1]);

        //1. NetworkAddress of the chatServer
        NetworkAddress chatServerAddress = new NetworkAddress(args[0], port);

        //2. Create a random NetworkAddress for the chatServerStub and one for the chatClientSkeleton
        MessageManager chatServerStubAddress = new MessageManager();
        MessageManager chatClientSkeletonAddress = new MessageManager();

        //3. To send messages to the chatServer we make use of a ChatServerStub
        ChatServer chatServer = new ChatServerStub(chatServerAddress, chatServerStubAddress);

        //4. We create two people who can chat together! It is a groupchat!
        ChatClient chatClient1 = new ChatClientImpl("Michael", chatServer, chatClientSkeletonAddress.getMyAddress());
        new ChatFrame(chatClient1);
        ChatClient chatClient2 = new ChatClientImpl("Quirine", chatServer, chatClientSkeletonAddress.getMyAddress());
        new ChatFrame(chatClient2);

        //5. The server will send receive messages. Therefore we have a chatClientSkeleton who listends
        //   to its port. The chatSkeleton has a list of chatClients, because each client must receive
        //   the message.
        List<ChatClient> chatClients = new ArrayList<>();
        chatClients.add(chatClient1);
        chatClients.add(chatClient2);


        ChatClientSkeleton chatClientSkeleton1 = new ChatClientSkeleton(chatClients, chatClientSkeletonAddress);


        //  This ChatSkeleton runs on a different thread! If not this will block the execution of the lines underneath.
        Thread thread = new Thread(chatClientSkeleton1);

        //This is a Deamon thread, because it is a non-blocking thread. This means if the JVM stops, this thread
        //will not prevent it. A deamon thread is like a background service.
        thread.setDaemon(true);
        thread.start();

        //6. We register two clients so they can chat together!
        ((ChatClientImpl) chatClient1).register();
        ((ChatClientImpl) chatClient2).register();

    }
}
