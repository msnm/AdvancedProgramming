package be.kdg.gedistribueerde.server.server;

import be.kdg.gedistribueerde.server.communication.MessageManager;
import be.kdg.gedistribueerde.server.communication.MethodCallMessage;
import be.kdg.gedistribueerde.server.communication.NetworkAddress;
import be.kdg.gedistribueerde.server.model.ChatPerson;

/**
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class ChatServerSkeleton {
    private MessageManager messageManager;
    private final ChatServer chatServer;


    /**
     * Constructs a new Skeleton
     */
    public ChatServerSkeleton() {
        this.messageManager = new MessageManager();
        this.chatServer = new ChatServerImpl();
        System.out.println(this.messageManager.getMyAddress());
    }

    /**
     * The loop that keeps the skeleton listening for new incomming messages
     */
    public void run() {
        while(true) {
            MethodCallMessage incommingRequest = messageManager.wReceive();
            handleRequest(incommingRequest);
        }
    }

    /**
     * Dispatches the incoming request to right handler
     * @param incommingRequest the request that is being handled
     */
    private void handleRequest(MethodCallMessage incommingRequest) {
        switch (incommingRequest.getMethodName()) {
            case "register": handleRegister(incommingRequest); break;
            case "unRegister": handleUnRegister(incommingRequest); break;
            case "send": handleSend(incommingRequest); break;
            default: handleException(incommingRequest);
        }
    }

    private void handleRegister(MethodCallMessage incommingRequest) {
        String ipAddress = incommingRequest.getParameter("ipAddress");
        Integer port = Integer.valueOf(incommingRequest.getParameter("port"));
        NetworkAddress networkAddress = new NetworkAddress(ipAddress, port);
        System.out.println("BEGIN: CHATSERVERSKELETON: handleRegister " + networkAddress.toString());
        chatServer.register(new ChatClientStub(networkAddress));
        sendEmptyReply(incommingRequest);
        System.out.println("END: CHATSERVERSKELETON: handleRegister " + networkAddress.toString());
    }

    private void handleUnRegister(MethodCallMessage incommingRequest) {
        String ipAddress = incommingRequest.getParameter("ipAddress");
        Integer port = Integer.valueOf(incommingRequest.getParameter("port"));
        NetworkAddress networkAddress = new NetworkAddress(ipAddress, port);
        chatServer.unRegister(new ChatClientStub(networkAddress));
        sendEmptyReply(incommingRequest);
    }

    private void handleSend(MethodCallMessage incommingRequest) {
        String name = incommingRequest.getParameter("name");
        String message = incommingRequest.getParameter("message");
        chatServer.send(name, message);
        sendEmptyReply(incommingRequest);
    }

    private void handleException(MethodCallMessage incommingRequest) { }

    /**
     * Sends reply with no return-value to the originator of a request.
     *
     * @param request the request that is aswered.
     */
    private void sendEmptyReply(MethodCallMessage request) {
        System.out.println("BEGIN: CHATSERVERSKELETON: sendEmptyReply");
        MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "result");
        reply.setParameter("result", "Ok");
        messageManager.send(reply, request.getOriginator());
        System.out.println("END: CHATSERVERSKELETON: sendEmptyReply");
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public void setMessageManager(MessageManager messageManager) {
        this.messageManager = messageManager;
    }
}
