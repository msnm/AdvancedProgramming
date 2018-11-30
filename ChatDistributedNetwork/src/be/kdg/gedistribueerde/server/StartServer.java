package be.kdg.gedistribueerde.server;

import be.kdg.gedistribueerde.server.server.ChatServerImpl;
import be.kdg.gedistribueerde.server.server.ChatServerSkeleton;

/**
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class StartServer {

    public static void main(String[] args) {
        ChatServerImpl chatServerImpl = new ChatServerImpl();
        ChatServerSkeleton chatServerSkeleton = new ChatServerSkeleton(chatServerImpl);
        chatServerSkeleton.run();
    }
}
