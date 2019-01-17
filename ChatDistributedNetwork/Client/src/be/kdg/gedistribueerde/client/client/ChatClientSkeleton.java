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
    private final ChatClient chatClient;

    public ChatClientSkeleton(ChatClient chatClient, MessageManager chatClientMessageManager) {
        this.messageManager = chatClientMessageManager;
        this.chatClient = chatClient;
        System.out.println(messageManager.getMyAddress() + " of CHATCLIENTSKELETON");
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
        }
        else if ("getName".equals(methodName)) {
            handleName(request);
        } else {
            System.out.println("ChatClientSkeleton: received an unknown request: " + request.getMethodName() + " " + request.getOriginator() );
            System.out.println(request);
        }
    }

    private void handleReceive(MethodCallMessage incommingRequest) {
        System.out.println("BEGIN: CHATCLIENTSKELTON: handleReceive");
        String message = incommingRequest.getParameter("message");
        chatClient.receive(message);
        sendEmptyReply(incommingRequest);
        System.out.println("END: CHATCLIENTSKELTON: handleReceive");
    }

    private void handleName(MethodCallMessage incomingRequest) {
        System.out.println("BEGIN: CHATCLIENTSKELETON: handleName");
        String name = chatClient.getName();
        MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "result");
        reply.setParameter("name", name);
        messageManager.send(reply, incomingRequest.getOriginator());
        System.out.println("END: CHATCLIENTSKELETON: handleName");
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
