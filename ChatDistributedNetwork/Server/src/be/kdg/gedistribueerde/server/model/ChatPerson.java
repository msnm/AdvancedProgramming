package be.kdg.gedistribueerde.server.model;

import be.kdg.gedistribueerde.server.communication.NetworkAddress;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatPerson that = (ChatPerson) o;

        if (networkAddress != null ? !networkAddress.equals(that.networkAddress) : that.networkAddress != null)
            return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = networkAddress != null ? networkAddress.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s:%n" +
                "   Name: %s%n" +
                "   NetworkAddress: %s%n"
                ,this.getClass().getSimpleName(), this.name, this.networkAddress);
    }
}
