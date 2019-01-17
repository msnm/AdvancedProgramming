package be.kdg.gedistribueerde.server.server;


import be.kdg.gedistribueerde.client.model.ChatPerson;

/**
 * Interface for the ChatServer component
 * @author Michael
 * @project ChatDistributedNetwork
 */
public interface ChatServer {

    /**
     * Registers a person to the running ChatServer
     */
    void register();

    /**
     * Unregisters a person from the running ChatServer
     */
    void unRegister();


    /**
     * Sends the message to all the clients
     * @param message the message to be send
     */
    void send(String message);
}
