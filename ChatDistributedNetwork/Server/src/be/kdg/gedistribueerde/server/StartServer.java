package be.kdg.gedistribueerde.server;

import be.kdg.gedistribueerde.server.communication.MessageManager;
import be.kdg.gedistribueerde.server.server.ChatServerImpl;
import be.kdg.gedistribueerde.server.server.ChatServerSkeleton;

/**
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class StartServer {

    public static void main(String[] args) {
        ChatServerSkeleton chatServerSkeleton = new ChatServerSkeleton();
        chatServerSkeleton.run();
    }
}
