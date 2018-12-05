package be.kdg.distrib;

import be.kdg.distrib.skeletonFactory.Skeleton;
import be.kdg.distrib.skeletonFactory.SkeletonFactory;
import be.kdg.distrib.communication.MessageManager;
import be.kdg.distrib.communication.MethodCallMessage;
import be.kdg.distrib.communication.NetworkAddress;
import be.kdg.distrib.testclasses.TestImplementation;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestSkeletonFactory {
    private TestImplementation testImplementation;
    private Skeleton skeleton;
    private MessageManager messageManager;
    private NetworkAddress myAddress;

    @Before
    public void setup() {
        testImplementation = new TestImplementation();
        skeleton = (Skeleton) SkeletonFactory.createSkeleton(testImplementation);
        messageManager = new MessageManager();
        myAddress = messageManager.getMyAddress();
    }

    @Test
    public void testCreateSkeletonWithValidAddress() {
        System.out.println("START: testCreateSkeletonWithValidAddress");
        assertNotNull(skeleton);
        NetworkAddress address = skeleton.getAddress();
        assertNotNull(address);
        assertTrue(address.getIpAddress().matches("^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$"));
        assertTrue(address.getPortNumber()>1023);
        System.out.printf("END: testCreateSkeletonWithValidAddress%n%n");
    }

    @Test
    public void testVoidMethod() {
        System.out.println("START: testVoidMethod");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod1");
        skeleton.handleRequest(message);
        assertEquals("void", testImplementation.getS());
        System.out.printf("END: testVoidMethod%n%n");
    }

    @Test(expected = RuntimeException.class)
    public void testWrongMethodName() {
        System.out.println("START: testWrongMethodName");
        MethodCallMessage message = new MethodCallMessage(myAddress, "nonExistingMethodName");
        skeleton.handleRequest(message);
        System.out.printf("END: testWrongMethodName%n%n");
    }

    @Test(timeout = 1000)
    public void testEmptyReply() {
        System.out.println("START: testEmptyReply");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod1");
        skeleton.handleRequest(message);
        MethodCallMessage reply = messageManager.wReceive();
        assertNotNull(reply.getParameter("result"));
        assertEquals("Ok", reply.getParameter("result"));
        System.out.printf("END: testEmptyReply%n%n");
    }


    private interface MyLambda {
        boolean operator();
    }

    private void waitUntil(int timeoutmsec, MyLambda l) {
        long endTime = System.currentTimeMillis()+timeoutmsec;
        while(System.currentTimeMillis()<endTime) {
            boolean b = l.operator();
            if (b) return;
        }
        throw new RuntimeException("timeout!");
    }

    @Test(timeout = 1000)
    public void testRunMethodSpawnThread() {
        System.out.println("START: testRunMethodSpawnThread");
        int numberOfThreads = Thread.getAllStackTraces().keySet().size();
        skeleton.run();
        int newNumber = Thread.getAllStackTraces().keySet().size();
        assertEquals("run method should create new thread", numberOfThreads+1, newNumber);
        System.out.printf("END: testRunMethodSpawnThread%n%n");
    }

    @Test(timeout = 1000)
    public void testRunMethodOneRequest() {
        System.out.println("START: testRunMethodOneRequest");
        skeleton.run();
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod1");
        messageManager.send(message, skeleton.getAddress());
        waitUntil(1000, () -> "void".equals(testImplementation.getS()));
        MethodCallMessage reply = messageManager.wReceive();
        assertNotNull(reply.getParameter("result"));
        assertEquals("Ok", reply.getParameter("result"));
        System.out.printf("END: testRunMethodOneRequest%n%n");
    }

    @Test(timeout = 1000)
    public void testRunMethodMultipleRequests() {
        System.out.println("START: testRunMethodMultipleRequests");
        skeleton.run();
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod1");
        messageManager.send(message, skeleton.getAddress());
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("Ok", reply.getParameter("result"));
        messageManager.send(message, skeleton.getAddress());
        reply = messageManager.wReceive();
        assertEquals("Ok", reply.getParameter("result"));
        System.out.printf("END: testRunMethodMultipleRequests%n%n");
    }

    @Test(expected = RuntimeException.class)
    public void testMessageWithWrongNumberOfParams() {
        System.out.println("START: testMessageWithWrongNumberOfParams");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod1");
        message.setParameter("arg0", "bla");
        skeleton.handleRequest(message);
        System.out.printf("END: testMessageWithWrongNumberOfParams%n%n");
    }

    @Test(timeout = 1000)
    public void testMethodWithParam() {
        System.out.println("START: testMethodWithParam");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod2");
        message.setParameter("arg0", "test");
        skeleton.handleRequest(message);
        assertEquals("test", testImplementation.getS());
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("Ok", reply.getParameter("result"));
        System.out.printf("END: testMethodWithParam%n%n");
    }

    @Test(expected = RuntimeException.class)
    public void testMessageWithWrongParamName() {
        System.out.println("START: testMessageWithWrongParamName");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod2");
        message.setParameter("naam", "test");
        skeleton.handleRequest(message);
        System.out.printf("END: testMessageWithWrongParamName%n%n");
    }

    @Test(timeout = 1000)
    public void testMethodWithIntParam() {
        System.out.println("START: testMethodWithIntParam");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod9");
        message.setParameter("arg0", "42");
        skeleton.handleRequest(message);
        assertEquals(42, testImplementation.getI());
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("Ok", reply.getParameter("result"));
        System.out.printf("END: testMethodWithIntParam%n%n");
    }


    @Test(expected = RuntimeException.class)
    public void testMessageWithWrongParamType() {
        System.out.println("START: testMessageWithWrongParamType");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod9");
        message.setParameter("arg0", "no integer");
        skeleton.handleRequest(message);
        System.out.printf("END: testMessageWithWrongParamType%n%n");
    }

    @Test(timeout = 1000)
    public void testMethodWithManyParams() {
        System.out.println("START: testMethodWithManyParams");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod3");
        message.setParameter("arg0", "42");
        message.setParameter("arg1", "testString");
        message.setParameter("arg2", "42.5");
        message.setParameter("arg3", "true");
        message.setParameter("arg4", "a");
        skeleton.handleRequest(message);
        assertEquals(42, testImplementation.getI());
        assertEquals("testString", testImplementation.getS());
        assertTrue(42.5==testImplementation.getD());
        assertTrue(testImplementation.isB());
        assertEquals('a', testImplementation.getC());
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("Ok", reply.getParameter("result"));
        System.out.printf("END: testMethodWithManyParams%n%n");
    }

    @Test(timeout = 1000)
    public void testObjectAsParameter() {
        System.out.println("START: testObjectAsParameter");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod4");
        message.setParameter("arg0.integer", "42");
        message.setParameter("arg0.string", "teststring");
        message.setParameter("arg0.bool", "true");
        message.setParameter("arg0.character", "a");
        skeleton.handleRequest(message);
        assertEquals(42, testImplementation.getI());
        assertEquals("teststring", testImplementation.getS());
        assertTrue(testImplementation.isB());
        assertEquals('a', testImplementation.getC());
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("Ok", reply.getParameter("result"));
        System.out.printf("END: testObjectAsParameter%n%n");
    }

    @Test(timeout = 1000)
    public void testReturnValue() {
        System.out.println("START: testReturnValue");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod5");
        skeleton.handleRequest(message);
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("Yes", reply.getParameter("result"));
        System.out.printf("END: testReturnValue%n%n");
    }

    @Test(timeout = 1000)
    public void testIntReturnValue() {
        System.out.println("START: testIntReturnValue");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod6");
        skeleton.handleRequest(message);
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("100", reply.getParameter("result"));
        System.out.printf("END: testIntReturnValue%n%n");
    }

    @Test(timeout = 1000)
    public void testCharReturnValue() {
        System.out.println("START: testCharReturnValue");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod7");
        skeleton.handleRequest(message);
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("r", reply.getParameter("result"));
        System.out.printf("END: testCharReturnValue%n%n");
    }

    @Test(timeout = 1000)
    public void testBoolReturnValue() {
        System.out.println("START: testBoolReturnValue");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod8");
        skeleton.handleRequest(message);
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("true", reply.getParameter("result"));
        System.out.printf("END: testBoolReturnValue%n%n");
    }

    @Test(timeout = 1000)
    public void testObjectReturnValue() {
        System.out.println("START: testObjectReturnValue");
        MethodCallMessage message = new MethodCallMessage(myAddress, "testMethod11");
        skeleton.handleRequest(message);
        MethodCallMessage reply = messageManager.wReceive();
        assertEquals("hoehoe", reply.getParameter("result.string"));
        assertEquals("p", reply.getParameter("result.character"));
        assertEquals("97", reply.getParameter("result.integer"));
        assertEquals("false", reply.getParameter("result.bool"));
        System.out.printf("END: testObjectReturnValue%n%n");
    }


}
