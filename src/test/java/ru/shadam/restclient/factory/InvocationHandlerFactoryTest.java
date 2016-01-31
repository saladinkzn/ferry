package ru.shadam.restclient.factory;

import com.google.common.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shadam.restclient.analyze.InterfaceContext;
import ru.shadam.restclient.analyze.MethodContext;
import ru.shadam.restclient.annotations.Param;
import ru.shadam.restclient.annotations.Url;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * @author sala
 */
public class InvocationHandlerFactoryTest {

    @Test
    public void createInvocationHandlerEmptyInterface() throws Exception {
        final MethodExecutorFactory methodExecutorFactory = Mockito.mock(MethodExecutorFactory.class);
        final InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory(methodExecutorFactory);
        invocationHandlerFactory.createInvocationHandler(EmptyInterface.class);
        //
        final ArgumentCaptor<MethodContext> methodContextArgumentCaptor = ArgumentCaptor.forClass(MethodContext.class);
        Mockito.verify(methodExecutorFactory, Mockito.never()).getRequestExecutor(methodContextArgumentCaptor.capture());
    }

    @Test
    public void createInvocationHandlerEmptyMethodsInterface() {
        final MethodExecutorFactory methodExecutorFactory = Mockito.mock(MethodExecutorFactory.class);
        final InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory(methodExecutorFactory);
        invocationHandlerFactory.createInvocationHandler(EmptyMethodsInterface.class);
        //
        final ArgumentCaptor<MethodContext> methodContextArgumentCaptor = ArgumentCaptor.forClass(MethodContext.class);
        Mockito.verify(methodExecutorFactory, Mockito.times(2)).getRequestExecutor(methodContextArgumentCaptor.capture());
        //
        final List<MethodContext> allValues = methodContextArgumentCaptor.getAllValues();
        Assert.assertEquals(2, allValues.size());
        //
        final MethodContext firstCallMethodContext = allValues.get(0);
        Assert.assertEquals("http://example.com", firstCallMethodContext.url());
        Assert.assertEquals("GET", firstCallMethodContext.method());
        Assert.assertTrue(firstCallMethodContext.params().isEmpty());
        Assert.assertTrue(firstCallMethodContext.indexToParamMap().isEmpty());
        Assert.assertEquals(new TypeToken<Map<String, Object>>(){}.getType(), firstCallMethodContext.returnType());
    }

    @Test
    public void getInterfaceContextEmptyInterface() throws Exception {
        final InterfaceContext interfaceContext = InvocationHandlerFactory.getInterfaceContext(EmptyInterface.class);
        Assert.assertEquals("", interfaceContext.baseUrl());
        Assert.assertEquals("GET", interfaceContext.defaultMethod());
    }

    @Test
    public void getInterfaceContextBaseUrlInterface() {
        final InterfaceContext interfaceContext1 = InvocationHandlerFactory.getInterfaceContext(BaseUrlInterface.class);
        Assert.assertEquals("http://example.com", interfaceContext1.baseUrl());
        Assert.assertEquals("GET", interfaceContext1.defaultMethod());
    }

    @Test
    public void getMethodContext() throws Exception {
        final InterfaceContext interfaceContext = InvocationHandlerFactory.getInterfaceContext(EmptyMethodsInterface.class);
        final MethodContext noParamsMethod = InvocationHandlerFactory.getMethodContext(interfaceContext, EmptyMethodsInterface.class.getMethod("noParamsMethods"));
        Assert.assertEquals("GET", noParamsMethod.method());
        Assert.assertEquals("http://example.com", noParamsMethod.url());
        Assert.assertTrue(noParamsMethod.params().isEmpty());
        Assert.assertTrue(noParamsMethod.indexToParamMap().isEmpty());
        Assert.assertEquals((new TypeToken<Map<String, Object>>() {}).getType(), noParamsMethod.returnType());
        //
        final MethodContext paramsMethod = InvocationHandlerFactory.getMethodContext(interfaceContext, EmptyMethodsInterface.class.getMethod("paramsMethod", String.class, String.class));
        Assert.assertEquals("GET", paramsMethod.method());
        Assert.assertEquals("http://example.com", paramsMethod.url());
        final LinkedHashSet<String> params = paramsMethod.params();
        Assert.assertTrue(params.contains("param1"));
        Assert.assertTrue(params.contains("param2"));
        final Map<Integer, String> indexToParamMap = paramsMethod.indexToParamMap();
        Assert.assertEquals("param1", indexToParamMap.get(0));
        Assert.assertEquals("param2", indexToParamMap.get(1));

    }

    @Test
    public void getMethodContextBaseUrlMethodsInterface() throws NoSuchMethodException {
        final InterfaceContext interfaceContext1 = InvocationHandlerFactory.getInterfaceContext(BaseUrlMethodsInterface.class);
        final MethodContext noParamsMethod1 = InvocationHandlerFactory.getMethodContext(interfaceContext1, BaseUrlMethodsInterface.class.getMethod("noParamsMethod"));
        Assert.assertEquals("http://example.com", noParamsMethod1.url());
        //
        final MethodContext listMethod = InvocationHandlerFactory.getMethodContext(interfaceContext1, BaseUrlMethodsInterface.class.getMethod("listMethod"));
        Assert.assertEquals("http://example.com/list", listMethod.url());
    }

    private interface EmptyInterface { }

    @Url("http://example.com")
    private interface BaseUrlInterface { }

    private interface EmptyMethodsInterface {
        @Url("http://example.com")
        public Map<String, Object> noParamsMethods();

        @Url("http://example.com")
        Map<String, Object> paramsMethod(@Param("param1") String param1, @Param("param2") String param2);
    }

    @Url("http://example.com")
    private interface BaseUrlMethodsInterface {

        public String noParamsMethod();

        @Url("/list")
        public List listMethod();
    }
}