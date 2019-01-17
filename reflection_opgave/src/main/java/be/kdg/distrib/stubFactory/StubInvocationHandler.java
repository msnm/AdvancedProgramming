package be.kdg.distrib.stubFactory;

import be.kdg.distrib.communication.MessageManager;
import be.kdg.distrib.communication.MethodCallMessage;
import be.kdg.distrib.communication.NetworkAddress;
import be.kdg.distrib.util.CastUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author Michael
 * @project distrib
 * <p>
 * Given an object and a method, the StubInvocationHandler will wrap this method with logic to invoke a TCP/IP request
 */
public class StubInvocationHandler implements InvocationHandler {

    // https://www.baeldung.com/java-dynamic-proxies

    private final NetworkAddress networkAddressDestinee;
    private final MessageManager messageManager;

    public StubInvocationHandler(String ipAddress, int port) {
        this.networkAddressDestinee = new NetworkAddress(ipAddress, port);
        this.messageManager = new MessageManager();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.printf("BEGIN: invoke(%s, %s)%n", proxy.getClass().getSimpleName(), method.getName());

        //1. Set our address and the method we want to reach at the server
        MethodCallMessage methodCallMessage = new MethodCallMessage(messageManager.getMyAddress(), method.getName());

        //2. Add parameters for every arg in args.
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (CastUtil.isPrimitiveOrWrapper(args[i])) {
                    methodCallMessage.setParameter("arg".concat(Integer.toString(i)), args[i].toString());
                } else {
                    // Are interested in all fields!
                    Field[] fieldsOfArg = args[i].getClass().getDeclaredFields();
                    for (int j = 0; j < fieldsOfArg.length; j++) {
                        fieldsOfArg[j].setAccessible(true);
                        methodCallMessage.setParameter("arg".concat(Integer.toString(i)).concat(".")
                                .concat(fieldsOfArg[j].getName()), fieldsOfArg[j].get(args[i]).toString());
                    }
                }
            }
        }
        //3. Send the message
        System.out.println("Stub sends message to skeleton: " + methodCallMessage.toString() + " address skeleton " + networkAddressDestinee.toString());
        messageManager.send(methodCallMessage, networkAddressDestinee);

        //4. If there is a return value, we wait for a reply and cast the reply type
        if (!method.getReturnType().equals(Void.TYPE)) {
            System.out.println("The return type is not VOID it is " + method.getReturnType().getSimpleName());
            MethodCallMessage reply = messageManager.wReceive();
            System.out.println("The reply is "+ reply.toString());
            if (!"result".equals(reply.getMethodName())) return null;
            Map<String, String> resultMap = reply.getParameters();

            //4.1 Need to check whether it is primitive/wrapper versus other object
            Class aClass = Class.forName(CastUtil.classForNameWrapPrimitives(method.getReturnType()));
            Object returnObject = null;

            if(CastUtil.isPrimitiveOrWrapper(aClass)) {
                String result = resultMap.get("result");
                returnObject = CastUtil.castResultToGivenType(result, method.getReturnType());
            }
            else {
                returnObject = aClass.newInstance();
                List<String> methodNames = Arrays.stream(aClass.getMethods()).map(v -> v.getName()).collect(Collectors.toList());
                for(Map.Entry<String, String> entry : resultMap.entrySet()) {
                    String fieldName = entry.getKey().substring("result.".length());
                    String setterName = "set".concat(fieldName.substring(0,1).toUpperCase()).concat(fieldName.substring(1));
                    if(methodNames.contains(setterName)) {
                        Field field = returnObject.getClass().getDeclaredField(fieldName);
                        field.setAccessible(true);
                        field.set(returnObject, CastUtil.castResultToGivenType(entry.getValue(),field.getType()));
                    }
                }
            }
            return  returnObject;
        }
        else {
            System.out.println("Waiting for empty reply");
            checkEmptyReply();
        }

        System.out.printf("END: invoke(%s, %s)%n%n", proxy.getClass().getSimpleName(), method.getName());
        return null;
    }

    private void checkEmptyReply() {
        String value = "";
        while (!"Ok".equals(value)) {
            MethodCallMessage reply = messageManager.wReceive();
            if (!"result".equals(reply.getMethodName())) {
                System.out.println("Waiting for empty reply: methodname" +  reply.getMethodName());
                continue;
            }
            value = reply.getParameter("result");
        }
    }




}
