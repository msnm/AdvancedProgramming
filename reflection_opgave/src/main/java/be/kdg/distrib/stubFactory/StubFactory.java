package be.kdg.distrib.stubFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author Michael
 * @project distrib
 *
 * The StubFactory creates a stub for a given interface. This stub will be used on the client side
 * to send messages to a remote server.
 */
public class StubFactory {

    /**
     * Generates a stub implementation of the myInterface
     * @param myInterface used to create a stub for each method
     * @param ipAddress the ipAddress of the remote server where the stub messages needs to be send to
     * @param port  the port number of the application on the remote server
     * @return returns a stub implementation of the myInterface
     */
    public static Object createStub(Class myInterface, String ipAddress, int port) {
        System.out.printf("BEGIN: createStub(%s, %s, %d)%n", myInterface.getSimpleName(), ipAddress, port);

        InvocationHandler invocationHandler = new StubInvocationHandler(ipAddress, port);
        Object result = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {myInterface}, invocationHandler);

        System.out.printf("END: createStub(%s, %s, %d)%n", myInterface.getSimpleName(), ipAddress, port);
        return result;
    }
}
