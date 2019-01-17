package be.kdg.gedistribueerde.client.client;

import be.kdg.gedistribueerde.client.communication.MessageManager;
import be.kdg.gedistribueerde.client.communication.MethodCallMessage;
import be.kdg.gedistribueerde.client.communication.NetworkAddress;
import be.kdg.gedistribueerde.client.model.ChatPerson;
import be.kdg.gedistribueerde.server.server.ChatServer;

/**
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class ChatServerStub implements ChatServer {
    private final NetworkAddress chatServerNetworkAddress;
    private final MessageManager messageManager;
    private final NetworkAddress clientNetworkAddress;


    /**
     * Constructs a new ChatServerStub using the address of the real component
     * @param chatServerAddress
     */
    public ChatServerStub(NetworkAddress chatServerAddress, NetworkAddress clientNetworkAddress) {
        this.chatServerNetworkAddress = chatServerAddress;
        this.messageManager = new MessageManager();
        this.clientNetworkAddress = clientNetworkAddress;
        System.out.println(messageManager.getMyAddress() + " of CHATSERVERSTUB");

    }

    @Override
    public void register(ChatClient chatClient) {
        System.out.println("BEGIN: CHATSERVERSTUB");
        //1. Creating the messageFormat that goes over the wire
        MethodCallMessage message = new MethodCallMessage(messageManager.getMyAddress(),"register");
        message.setParameter("port", Integer.toString(this.clientNetworkAddress.getPortNumber()));
        message.setParameter("ipAddress", this.clientNetworkAddress.getIpAddress());
        //2. Sending the message from the application layer down to the physical layer to the destinee
        messageManager.send(message, chatServerNetworkAddress);
        //3. The call is synchronous thus we wait for a positive or negative response code before we close the thread
        checkEmptyReply();
        System.out.println("END: CHATSERVERSTUB");
    }

    @Override
    public void unRegister(ChatClient chatClient) {
        MethodCallMessage message = new MethodCallMessage(messageManager.getMyAddress(),"unRegister");
        message.setParameter("port", Integer.toString(this.clientNetworkAddress.getPortNumber()));
        message.setParameter("ipAddress", this.clientNetworkAddress.getIpAddress());
        messageManager.send(message, chatServerNetworkAddress);
        checkEmptyReply();
    }


    @Override
    public void send(String name, String message) {
        MethodCallMessage messageCall = new MethodCallMessage(messageManager.getMyAddress(),"send");
        messageCall.setParameter("name", name);
        messageCall.setParameter("message", message);
        messageManager.send(messageCall, chatServerNetworkAddress);
        checkEmptyReply();
    }

    /**
     * Waits for a reply and checks if it contains no return-value.
     */
    private void checkEmptyReply() {
        System.out.println("BEGIN: CHATSERVERSTUB: Checking for Empty Reply");
        String value = "";
        while (!"Ok".equals(value)) {
            MethodCallMessage reply = messageManager.wReceive();
            if (!"result".equals(reply.getMethodName())) {
                continue;
            }
            value = reply.getParameter("result");
        }
        System.out.println("END: CHATSERVERSTUB: Checking for Empty Reply");
    }
}
