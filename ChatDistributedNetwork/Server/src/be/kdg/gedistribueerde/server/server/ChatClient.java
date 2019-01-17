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


    /**
     * To receive the name of the chatclient
     * @return
     */
    String getName();
}
