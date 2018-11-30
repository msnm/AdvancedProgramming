package be.kdg.gedistribueerde.server.server;

/**
 * @author Michael
 * @project ChatDistributedNetwork
 * Interface for the ChatClient component
 */
public interface ChatClient {


    /**
     * A chatclient receives messages from a chatserver
     * @param message
     */
    void receive(String message);
}
