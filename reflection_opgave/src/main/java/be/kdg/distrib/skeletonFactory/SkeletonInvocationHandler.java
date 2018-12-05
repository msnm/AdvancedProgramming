package be.kdg.distrib.skeletonFactory;

import be.kdg.distrib.communication.MessageManager;
import be.kdg.distrib.communication.MethodCallMessage;
import be.kdg.distrib.communication.NetworkAddress;
import be.kdg.distrib.util.CastUtil;
import java.util.List;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Michael
 * @project distrib
 */
public class SkeletonInvocationHandler implements InvocationHandler {
    private final MessageManager messageManager;
    private final Map<String, Method> methodsOfTarget;

    private final Object target;

    public SkeletonInvocationHandler(Object object) {
        this.messageManager = new MessageManager();
        this.target = object;
        methodsOfTarget = new HashMap<>();
        // We only store the public methods!
        Arrays.stream(object.getClass().getMethods()).forEach(v -> methodsOfTarget.put(v.getName(), v));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.printf("BEGIN: %s.invoke(%s)%n", this.getClass().getSimpleName(), method.getName());
        String methodName = method.getName();

        if(methodName.equals("run")) {
            this.run();
        }

        if(methodName.equals("handleRequest")) {
            try {
                return this.handleRequest((MethodCallMessage) args[0]);
            }
            catch (ClassCastException ex) {
                System.err.println("ClasscastException: Argument should be of type " + MethodCallMessage.class.getSimpleName() + " ex:"+ex.getMessage());
            }

        }
        if(methodName.equals("getAddress")) {
            return  this.getAddress();
        }
        return null;
    }

    private NetworkAddress getAddress() {
        System.out.printf("BEGIN: %s.getAddress)%n", this.getClass().getSimpleName());
        return messageManager.getMyAddress();
    }

    private void run() {
        System.out.printf("BEGIN: %s.run()%n", this.getClass().getSimpleName());
        Thread thread = new Thread() {
            @Override
            public void  run() {
                while(true) {
                    MethodCallMessage incommingRequest = messageManager.wReceive();
                    try {
                        handleRequest(incommingRequest);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        //thread.setDaemon(true);
        thread.start();


    }

    private Object handleRequest(MethodCallMessage request) throws Exception {
        System.out.printf("BEGIN: handleRequest(%s)%n", request.toString());
        //1. Need to determine which method we need to invoke on the target
        Method methodTarget = methodsOfTarget.get(request.getMethodName());

        //2 Retrieve the parameters from the request
        Map<String, String> requestParameters = request.getParameters();
        Object[] args = null;
        //3. We need to determine the arguments of the method and make a distinction between primitive/wrappers and
        //   other objects.

        if(methodTarget!=null && countParameters(methodTarget.getParameters()) == requestParameters.size()) {
            Parameter[] methodParams = methodTarget.getParameters(); //returns in declaration order!
            //3.1 For every parameter we create an object.
            //   The value of the object is found in request.getParameters.
            args = new Object[methodParams.length];
            int count = 0;
            for(Parameter param : methodParams) {
                Object argument = null;
                // 3.2 Need to check if the object is nor a wrapper nor a primitive!
                if(!CastUtil.isPrimitiveOrWrapper(param.getType())) {
                    Class classParam = Class.forName(param.getType().getName());
                    argument = classParam.newInstance();
                    /*
                    //TODO: WAAROM WERKT DIT NIET?
                    for(Method method: classParam.getMethods()) {
                        if(method.getName().startsWith("set")) {
                            String methodName = method.getName();
                            String searchRequestParam = "arg.".concat(Integer.toString(count)).concat(methodName.substring(3).toLowerCase());
                            method.invoke(objectParam, CastUtil.castResultToGivenType(requestParameters.get(searchRequestParam), classParam));
                        }
                    }
                    */
                    for(Field field : argument.getClass().getDeclaredFields()) {
                        String searchRequestParam = "arg".concat(Integer.toString(count)).concat(".").concat(field.getName());
                        System.out.println("searchRequest param " + searchRequestParam);
                        if(requestParameters.containsKey(searchRequestParam)) {
                            field.setAccessible(true);
                            field.set(argument, CastUtil.castResultToGivenType(requestParameters.get(searchRequestParam), field.getType()));
                        }
                    }
                }
                else {
                    // 3.3 The param is a wrapper or primitive:
                    Class argumentClass = Class.forName(CastUtil.classForNameWrapPrimitives(param.getType()));
                    argument = CastUtil.castResultToGivenType(requestParameters.get(param.getName()), argumentClass);
                    System.out.printf("\t\tCreated %s with value %s%n", param.getName(), argument.toString());

                }
                args[count] = argument;
                count++;
            }
        }
        else {
            System.out.println("handleRequest: expcetion gooien. Methode niet gekend, of parameter mismatch");
            throw new Exception("Parameter mismatch between methodCallMessage and method " + methodTarget.getName());
        }

        //4. Invoking the method off the implementation
        System.out.printf("BEGIN: invoke(%s)%n", methodTarget.getName());
        Object result = methodTarget.invoke(target, args);
        System.out.printf("END: invoke(%s) with result %s%n", methodTarget.getName(), result!=null?result.toString():"void");

        //5. Sending a reply.
        //5.1 Sending an empty reply if result == null, otherwise we return the object
        if(methodTarget.getReturnType().equals(Void.TYPE)) {
            this.sendEmptyReply(request);
        }
        else {
            MethodCallMessage reply = new MethodCallMessage(messageManager.getMyAddress(), "result");
            if(CastUtil.isPrimitiveOrWrapper(methodTarget.getReturnType())) {
                reply.setParameter("result", result.toString());
            }
            else {
                List<String> methodNames = Arrays.stream(result.getClass().getMethods()).map(v -> v.getName()).collect(Collectors.toList());
                for(Field field : result.getClass().getDeclaredFields()) {
                    String searchSetter = "set".concat(field.getName().substring(0,1).toUpperCase()).concat(field.getName().substring(1));
                    if(methodNames.contains(searchSetter)) {
                        field.setAccessible(true);
                        String param = field.get(result).toString();
                        reply.setParameter("result.".concat(field.getName()), param);
                    }

                }

            }
            messageManager.send(reply, request.getOriginator());

        }
        //5.2. Sending a reply with the return value as parameter
        return result;
    }

    private int countParameters(Parameter[] parameters) {
        int countParameters = 0;
        for(Parameter parameter : parameters) {
            countParameters = parameters.length;
            if(!CastUtil.isPrimitiveOrWrapper(parameter.getType())) {
                countParameters--; //Otherwise we count the object as a parameter!
                //We assume that if a setter exist, there will be a parameter!
                for(Method method : parameter.getType().getMethods()) {
                    if(method.getName().startsWith("get")) {
                        countParameters++;
                    }
                }
            }
        }
        return  countParameters;
    }

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
}
