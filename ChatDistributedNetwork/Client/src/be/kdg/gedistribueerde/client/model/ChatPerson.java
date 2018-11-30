package be.kdg.gedistribueerde.client.model;


import be.kdg.gedistribueerde.client.communication.NetworkAddress;

/**
 * Entity class representing a ChatPerson
 *
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class ChatPerson {

    private NetworkAddress networkAddress;
    private String name;

    public ChatPerson(String name) {
        this.name = name;
    }

    public NetworkAddress getNetworkAddress() {
        return networkAddress;
    }

    public void setNetworkAddress(NetworkAddress networkAddress) {
        this.networkAddress = networkAddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("%s:%n" +
                "   Name: %s%n" +
                "   NetworkAddress: %s%n"
                ,this.getClass().getSimpleName(), this.name, this.networkAddress);
    }
}
