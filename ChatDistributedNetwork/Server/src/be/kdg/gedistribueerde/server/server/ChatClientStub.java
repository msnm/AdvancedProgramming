package be.kdg.gedistribueerde.server.server;

import be.kdg.gedistribueerde.server.communication.MessageManager;
import be.kdg.gedistribueerde.server.communication.MethodCallMessage;
import be.kdg.gedistribueerde.server.communication.NetworkAddress;

/**
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class ChatClientStub implements ChatClient {
    private final MessageManager messageManager;
    private NetworkAddress chatClientAddress;

    public ChatClientStub(NetworkAddress chatClientAddress) {
        this.messageManager = new MessageManager();
        this.chatClientAddress = chatClientAddress;
    }

    @Override
    public void receive(String message) {
        System.out.println("BEGIN: CHATCLIENTSTUB: receive " + message +  "for networkaddress " + chatClientAddress.toString());
        MethodCallMessage methodCallMessage = new MethodCallMessage(messageManager.getMyAddress(), "receive");
        methodCallMessage.setParameter("message", message);
        messageManager.send(methodCallMessage, chatClientAddress);
        checkEmptyReply();
        System.out.println("END: CHATCLIENTSTUB: receive " + message);
    }

    @Override
    public String getName() {
        System.out.println("BEGIN: CHATCLIENTSTUB: getName for networkaddress " + chatClientAddress.toString());
        MethodCallMessage methodCallMessage = new MethodCallMessage(messageManager.getMyAddress(), "getName");
        messageManager.send(methodCallMessage, chatClientAddress);
        MethodCallMessage reply = messageManager.wReceive();
        if (!"result".equals(reply.getMethodName())) {
            return "";
        }
        System.out.println("BEGIN: CHATCLIENTSTUB: getName for networkaddress " + chatClientAddress.toString() + " returns " + reply.getParameter("name"));
        return reply.getParameter("name");
    }

    /**
     * Waits for a reply and checks if it contains no return-value.
     */
    private void checkEmptyReply() {
        System.out.println("BEGIN: CHATSCLIENTSTUB: checkEmptyReply");
        String value = "";
        while (!"Ok".equals(value)) {
            MethodCallMessage reply = messageManager.wReceive();
            if (!"result".equals(reply.getMethodName())) {
                continue;
            }
            value = reply.getParameter("result");
        }
        System.out.println("END: CHATSCLIENTSTUB: checkEmptyReply");
    }

    public NetworkAddress getChatClientAddress() {
        return chatClientAddress;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ChatClientStub that = (ChatClientStub) o;

        return chatClientAddress != null ? chatClientAddress.equals(that.chatClientAddress) : that.chatClientAddress == null;
    }

    @Override
    public int hashCode() {
        return chatClientAddress != null ? chatClientAddress.hashCode() : 0;
    }

    public void setChatClientAddress(NetworkAddress chatClientAddress) {
        this.chatClientAddress = chatClientAddress;
    }
}
