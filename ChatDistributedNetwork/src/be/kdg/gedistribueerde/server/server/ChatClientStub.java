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
        System.out.println("BEGIN: CHATSCLIENTSTUB: receive " + message);
        MethodCallMessage methodCallMessage = new MethodCallMessage(messageManager.getMyAddress(), "receive");
        methodCallMessage.setParameter("message", message);
        messageManager.send(methodCallMessage, chatClientAddress);
        checkEmptyReply();
        System.out.println("END: CHATSCLIENTSTUB: receive " + message);
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

    public void setChatClientAddress(NetworkAddress chatClientAddress) {
        this.chatClientAddress = chatClientAddress;
    }
}
