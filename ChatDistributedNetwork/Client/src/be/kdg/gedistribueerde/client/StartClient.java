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

        NetworkAddress chatServerAddress = new NetworkAddress(args[0], port);

        MessageManager clientsOnThisMachine = new MessageManager();
        ChatServer chatServer = new ChatServerStub(chatServerAddress, clientsOnThisMachine);

        ChatClientImpl chatClient1 = new ChatClientImpl("Michael", chatServer);
        new ChatFrame(chatClient1);
        chatClient1.register();
        ChatClientSkeleton chatClientSkeleton1 = new ChatClientSkeleton(chatClient1, clientsOnThisMachine);
        chatClientSkeleton1.run();
    }
}
