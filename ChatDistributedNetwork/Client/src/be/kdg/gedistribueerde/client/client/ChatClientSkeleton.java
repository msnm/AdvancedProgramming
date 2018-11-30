package be.kdg.gedistribueerde.client.client;

import be.kdg.gedistribueerde.client.communication.MessageManager;
import be.kdg.gedistribueerde.client.communication.MethodCallMessage;

import java.util.List;


/**
 * @author Michael
 * @project ChatDistributedNetwork
 */
public class ChatClientSkeleton implements Runnable {
    private final MessageManager messageManager;
    private final List<ChatClient> chatClients;

    public ChatClientSkeleton(List<ChatClient> chatClient, MessageManager messageManager) {
        this.messageManager = messageManager;
        this.chatClients = chatClient;
    }


    /**
     * The main loop for this skeleton.
     */
    @Override
    public void run() {
        while (true) {
            MethodCallMessage request = messageManager.wReceive();
            handleRequest(request);
        }
    }

    private void handleRequest(MethodCallMessage request) {
        String methodName = request.getMethodName();
        if ("receive".equals(methodName)) {
            handleReceive(request);
        } else {
            System.out.println("ChatClientSkeleton: received an unknown request:");
            System.out.println(request);
        }
    }

    private void handleReceive(MethodCallMessage incommingRequest) {
        System.out.println("BEGIN: CHATCLIENTSKELTON: handleReceive");
        String message = incommingRequest.getParameter("message");
        chatClients.stream().forEach(v -> v.receive(message));
        //sendEmptyReply(incommingRequest);
        System.out.println("END: CHATCLIENTSKELTON: handleReceive");
    }

    /**
     * Sends reply with no return-value to the originator of a request.
     *
     * @param request the request that is aswered.
     */
    private void sendEmptyReply(MethodCallMessage request) {
        System.out.println("BEGIN: CHATCLIENTSKELTON: sendEmptyReply");
        MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "result");
        reply.setParameter("result", "Ok");
        messageManager.send(reply, request.getOriginator());
        System.out.println("END: CHATCLIENTSKELTON: sendEmptyReply");
    }
}
