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

        //1. NetworkAddress of the chatServer
        NetworkAddress chatServerAddress = new NetworkAddress(args[0], port);

        //2. Create a random NetworkAddress for the chatServerStub and one for the chatClientSkeleton
        MessageManager chatClientSkeletonAddress = new MessageManager();

        //3. To send messages to the chatServer we make use of a ChatServerStub
        ChatServer chatServer = new ChatServerStub(chatServerAddress, chatClientSkeletonAddress.getMyAddress());

        //4. We create person1 named Michael
        ChatClient chatClient = new ChatClientImpl(chatServer, "Rosa");

        //5. The server will send "receive"  and "getName" messages. Therefore we have a chatClientSkeleton who listens.
        ChatClientSkeleton chatClientSkeleton = new ChatClientSkeleton(chatClient, chatClientSkeletonAddress);

        //6. Need to create a  GUI for the user
        new ChatFrame(chatClient);

        //7.  This ChatSkeleton runs on a different thread! If not this will block the execution of the lines underneath.
        Thread thread = new Thread(chatClientSkeleton);

        //This is a Deamon thread, because it is a non-blocking thread. This means if the JVM stops, this thread
        //will not prevent it. A deamon thread is like a background service.
        thread.setDaemon(true);
        thread.start();

        //8. We register the client!
        ((ChatClientImpl) chatClient).register();

    }
}
