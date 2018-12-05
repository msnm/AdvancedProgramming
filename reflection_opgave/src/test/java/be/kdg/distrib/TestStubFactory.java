package be.kdg.distrib;

import be.kdg.distrib.communication.MethodCallMessage;
import be.kdg.distrib.communication.NetworkAddress;
import be.kdg.distrib.stubFactory.StubFactory;
import be.kdg.distrib.testclasses.TestInterface;
import be.kdg.distrib.testclasses.TestInterface2;
import be.kdg.distrib.testclasses.TestObject;
import be.kdg.distrib.testclasses.TestSkeleton;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TestStubFactory {
    private int port;
    private TestSkeleton testSkeleton;

    @Before
    public void setup() {
        testSkeleton = new TestSkeleton();
        testSkeleton.start();
        NetworkAddress address = testSkeleton.getMyAddress();
        port = address.getPortNumber();
    }

    @Test
    public void testCreateClassWithRightMethods() {
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        stub.testMethod1();
    }

    @Test
    public void testCreateClassWithDerivedInterface() {
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface2.class, "127.0.0.1", port);
        stub.testMethod1();
    }

    @Test(expected = java.lang.ClassCastException.class)
    public void testTryToCastToWrongClass() {
        TestInterface stub = (TestInterface2) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        stub.testMethod1();
    }

    @Test
    public void testVoidVoid() throws InterruptedException {
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        stub.testMethod1();
        Thread.sleep(100);
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod1", message.getMethodName());
    }

    @Test
    public void testWithOneParam() throws InterruptedException {
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        stub.testMethod2("azerty");
        Thread.sleep(100);
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod2", message.getMethodName());
        assertEquals(1, message.getParameters().size());
        assertEquals("azerty", message.getParameter("arg0"));
    }

    @Test
    public void testWithIntParam() throws InterruptedException {
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        stub.testMethod9(42);
        Thread.sleep(100);
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod9", message.getMethodName());
        assertEquals(1, message.getParameters().size());
        assertEquals("42", message.getParameter("arg0"));
    }

    @Test
    public void testWithMoreParams() throws InterruptedException {
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        stub.testMethod3(42, "forty-two", 3.14, true, 'c');
        Thread.sleep(100);
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod3", message.getMethodName());
        assertEquals(5, message.getParameters().size());
        assertEquals("42", message.getParameter("arg0"));
        assertEquals("forty-two", message.getParameter("arg1"));
        assertEquals("3.14", message.getParameter("arg2"));
        assertEquals("true", message.getParameter("arg3"));
        assertEquals("c", message.getParameter("arg4"));
    }

    @Test
    public void testWithObjectParams() throws InterruptedException {
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        TestObject test = new TestObject("forty-two", 42, 'k', false);
        stub.testMethod4(test);
        Thread.sleep(100);
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod4", message.getMethodName());
        assertEquals(4, message.getParameters().size());
        assertEquals("forty-two", message.getParameter("arg0.string"));
        assertEquals("42", message.getParameter("arg0.integer"));
        assertEquals("k", message.getParameter("arg0.character"));
        assertEquals("false", message.getParameter("arg0.bool"));
    }

    @Test
    public void testWithStringReturnValue() {
        testSkeleton.sendStringReturnValue();
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        String s = stub.testMethod5();
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod5", message.getMethodName());
        assertEquals(0, message.getParameters().size());
        assertEquals("forty-two", s);
    }



    @Test
    public void testWithIntReturnValue() {
        testSkeleton.sendIntReturnValue();
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        int i = stub.testMethod6();
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod6", message.getMethodName());
        assertEquals(0, message.getParameters().size());
        assertEquals(42, i);
    }

    @Test
    public void testWithCharReturnValue() {
        testSkeleton.sendCharReturnValue();
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        char c = stub.testMethod7();
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod7", message.getMethodName());
        assertEquals(0, message.getParameters().size());
        assertEquals('K', c);
    }

    @Test
    public void testWithBooleanReturnValue() {
        testSkeleton.sendBooleanReturnValue();
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        boolean b = stub.testMethod8();
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod8", message.getMethodName());
        assertEquals(0, message.getParameters().size());
        assertEquals(true, b);
    }

    @Test
    public void testWithObjectReturnValue() {
        testSkeleton.sendObjectReturnValue();
        TestInterface stub = (TestInterface) StubFactory.createStub(TestInterface.class, "127.0.0.1", port);
        TestObject test = stub.testMethod11();
        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("testMethod11", message.getMethodName());
        assertEquals(0, message.getParameters().size());
        assertEquals('r', test.getCharacter());
        assertEquals("bloop", test.getString());
        assertEquals(123, test.getInteger());
        assertTrue(test.isBool());
    }

    @Test
    public void testWithEverything() {
        testSkeleton.sendObjectReturnValue();
        TestInterface2 stub = (TestInterface2) StubFactory.createStub(TestInterface2.class, "127.0.0.1", port);
        TestObject test = new TestObject("blah", 9, 'a', true);
        TestObject result = stub.fullBlownTestMethod("haha", test, 7, false);

        MethodCallMessage message = testSkeleton.getMessage();
        assertEquals("fullBlownTestMethod", message.getMethodName());
        assertEquals(7, message.getParameters().size());
        assertEquals("haha", message.getParameter("arg0"));
        assertEquals("blah", message.getParameter("arg1.string"));
        assertEquals("9", message.getParameter("arg1.integer"));
        assertEquals("a", message.getParameter("arg1.character"));
        assertEquals("true", message.getParameter("arg1.bool"));
        assertEquals("7", message.getParameter("arg2"));
        assertEquals("false", message.getParameter("arg3"));

        assertEquals("bloop", result.getString());
        assertEquals('r', result.getCharacter());
        assertEquals(123, result.getInteger());
        assertTrue(result.isBool());
    }
}
