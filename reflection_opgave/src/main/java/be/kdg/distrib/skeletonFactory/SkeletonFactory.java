package be.kdg.distrib.skeletonFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

/**
 * @author Michael
 * @project distrib
 *
 *  The Skeleton creates a skeleton for a given interface. This skeleton will be used on the server side
 *  to listen to incoming messages from clients.
 */
public class SkeletonFactory {

    public static Object createSkeleton(Object object) {
        InvocationHandler invocationHandler = new SkeletonInvocationHandler(object);
        return Proxy.newProxyInstance(Skeleton.class.getClassLoader(),
                new Class[] {Skeleton.class}, invocationHandler);

    }
}
