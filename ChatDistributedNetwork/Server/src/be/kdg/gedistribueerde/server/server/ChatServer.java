package be.kdg.gedistribueerde.server.server;



/**
 * Interface for the ChatServer component
 * @author Michael
 * @project ChatDistributedNetwork
 */
public interface ChatServer {

    /**
     * Registers a person to the running ChatServer
     * @param chatClient
     */
    void register(ChatClient chatClient);

    /**
     * Unregisters a person from the running ChatServer
     * @param chatClient
     */
    void unRegister(ChatClient chatClient);


    /**
     * Sends the message to all the clients
     * @param name the name of the sender
     * @param message the message to be send
     */
    void send(String name, String message);
}
