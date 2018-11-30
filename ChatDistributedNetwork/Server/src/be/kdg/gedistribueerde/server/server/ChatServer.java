package be.kdg.gedistribueerde.server.server;


import be.kdg.gedistribueerde.server.model.ChatPerson;

/**
 * Interface for the ChatServer component
 * @author Michael
 * @project ChatDistributedNetwork
 */
public interface ChatServer {

    /**
     * Registers a person to the running ChatServer
     * @param chatPerson has a name and an optional networkaddress
     */
    void register(ChatPerson chatPerson);

    /**
     * Unregisters a person from the running ChatServer
     * @param chatPerson has a name and an optional networkaddress
     */
    void unRegister(ChatPerson chatPerson);


    /**
     * Sends the message to all the clients
     * @param name the name of the sender
     * @param message the message to be send
     */
    void send(String name, String message);
}
