package ru.shadam.ferry.factory;

import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shadam.ferry.analyze.InterfaceContext;
import ru.shadam.ferry.analyze.MethodContext;
import ru.shadam.ferry.annotations.*;
import ru.shadam.ferry.factory.executor.MethodExecutorFactory;
import ru.shadam.ferry.implicit.ImplicitParameterProvider;

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
        final Map<String, ImplicitParameterProvider> implicitParameterProviderMap = Maps.newHashMap();
        final InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory(methodExecutorFactory, implicitParameterProviderMap);
        invocationHandlerFactory.createInvocationHandler(EmptyInterface.class);
        //
        final ArgumentCaptor<MethodContext> methodContextArgumentCaptor = ArgumentCaptor.forClass(MethodContext.class);
        Mockito.verify(methodExecutorFactory, Mockito.never()).getRequestExecutor(methodContextArgumentCaptor.capture());
    }

    @Test
    public void createInvocationHandlerEmptyMethodsInterface() {
        final MethodExecutorFactory methodExecutorFactory = Mockito.mock(MethodExecutorFactory.class);
        final Map<String, ImplicitParameterProvider> implicitParameterProviderMap = Maps.newHashMap();
        final InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory(methodExecutorFactory, implicitParameterProviderMap);
        invocationHandlerFactory.createInvocationHandler(EmptyMethodsInterface.class);
        //
        final ArgumentCaptor<MethodContext> methodContextArgumentCaptor = ArgumentCaptor.forClass(MethodContext.class);
        Mockito.verify(methodExecutorFactory, Mockito.times(2)).getRequestExecutor(methodContextArgumentCaptor.capture());
        //
        final List<MethodContext> allValues = methodContextArgumentCaptor.getAllValues();
        Assert.assertEquals(2, allValues.size());
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

    @Test
    public void getMethodContextMethodTestInterface() throws Exception {
        final InterfaceContext interfaceContext = InvocationHandlerFactory.getInterfaceContext(MethodTestInterface.class);
        Assert.assertEquals("POST", interfaceContext.defaultMethod());
        //
        final MethodContext defaultPostMethod = InvocationHandlerFactory.getMethodContext(interfaceContext, MethodTestInterface.class.getMethod("defaultPostMethod"));
        Assert.assertEquals("POST", defaultPostMethod.method());

        final MethodContext overrideMethod = InvocationHandlerFactory.getMethodContext(interfaceContext, MethodTestInterface.class.getMethod("overrideMethod"));
        Assert.assertEquals("GET", overrideMethod.method());
    }

    @Test
    public void getMethodContextImplicitTest() throws NoSuchMethodException {
        final Class<ImplicitTestInterface> interfaceClass = ImplicitTestInterface.class;
        final InterfaceContext interfaceContext = InvocationHandlerFactory.getInterfaceContext(interfaceClass);
        final MethodContext testMethod1 = InvocationHandlerFactory.getMethodContext(interfaceContext, interfaceClass.getMethod("testMethod1", Long.class));
        //
        final Map<String, String> constImplicitParams = testMethod1.constImplicitParams();
        Assert.assertNotNull(constImplicitParams);
        Assert.assertTrue(constImplicitParams.containsKey("v"));
        Assert.assertEquals("5.41", constImplicitParams.get("v"));
        //
        final MethodContext testMethod2 = InvocationHandlerFactory.getMethodContext(interfaceContext, ImplicitTestInterface.class.getMethod("testMethod2", Long.class));
        //
        final Map<String, String> providedImplicitParams = testMethod2.providedImplicitParams();
        Assert.assertNotNull(providedImplicitParams);
        Assert.assertTrue(providedImplicitParams.containsKey("access_token"));
        Assert.assertEquals("accessTokenProvider", providedImplicitParams.get("access_token"));
        //
        final MethodContext testMethod3 = InvocationHandlerFactory.getMethodContext(interfaceContext, ImplicitTestInterface.class.getMethod("testMethod3", Long.class));
        //
        final Map<String, String> constImplicitParams1 = testMethod3.constImplicitParams();
        final Map<String, String> providedImplicitParams1 = testMethod3.providedImplicitParams();
        Assert.assertTrue(constImplicitParams1.containsKey("v"));
        Assert.assertEquals("5.41", constImplicitParams1.get("v"));
        Assert.assertTrue(providedImplicitParams1.containsKey("access_token"));
        Assert.assertEquals("accessTokenProvider", providedImplicitParams1.get("access_token"));
    }

    @Test
    public void getInterfaceContextImplicitTest() throws Throwable {
        final Class<ClassLevelImplicitTestInterface> classLevelImplicitTestInterfaceClass = ClassLevelImplicitTestInterface.class;
        final InterfaceContext interfaceContext = InvocationHandlerFactory.getInterfaceContext(classLevelImplicitTestInterfaceClass);
        final Map<String, String> constImplicitParams = interfaceContext.constImplicitParams();
        Assert.assertNotNull(constImplicitParams);
        Assert.assertTrue(constImplicitParams.containsKey("v"));
        Assert.assertEquals("5.41", constImplicitParams.get("v"));
        //
        final Class<ClassLevelProvidedImplicitTestInterface> classLevelProvidedImplicitTestInterface = ClassLevelProvidedImplicitTestInterface.class;
        final InterfaceContext interfaceContext2 = InvocationHandlerFactory.getInterfaceContext(classLevelProvidedImplicitTestInterface);
        final Map<String, String> providedImplicitParams = interfaceContext2.providedImplicitParams();
        Assert.assertNotNull(providedImplicitParams);
        Assert.assertTrue(providedImplicitParams.containsKey("access_token"));
        Assert.assertEquals("accessTokenProvider", providedImplicitParams.get("access_token"));

        final Class<ClassLevelInplicitParamsTestInterface> classLevelInplicitParamsTestInterfaceClass = ClassLevelInplicitParamsTestInterface.class;
        final InterfaceContext interfaceContext3 = InvocationHandlerFactory.getInterfaceContext(classLevelInplicitParamsTestInterfaceClass);
        final Map<String, String> constImplicitParams2 = interfaceContext3.constImplicitParams();
        final Map<String, String> providedImplicitParams2 = interfaceContext3.providedImplicitParams();
        Assert.assertNotNull(constImplicitParams2);
        Assert.assertTrue(constImplicitParams2.containsKey("v"));
        Assert.assertEquals("5.41", constImplicitParams2.get("v"));
        Assert.assertNotNull(providedImplicitParams2);
        Assert.assertTrue(providedImplicitParams2.containsKey("access_token"));
        Assert.assertEquals("accessTokenProvider", providedImplicitParams2.get("access_token"));
    }

    @Test
    public void mixTypeAndMethodImplicitTest() throws Throwable {
        final Class<MixedImplicitParamsTestInterface> mixedImplicitParamsTestInterfaceClass = MixedImplicitParamsTestInterface.class;
        final InterfaceContext interfaceContext = InvocationHandlerFactory.getInterfaceContext(mixedImplicitParamsTestInterfaceClass);
        final MethodContext methodContext = InvocationHandlerFactory.getMethodContext(interfaceContext, mixedImplicitParamsTestInterfaceClass.getMethod("testMethod"));
        //
        final Map<String, String> constImplicitParams = methodContext.constImplicitParams();
        Assert.assertNotNull(constImplicitParams);
        Assert.assertTrue(constImplicitParams.containsKey("v"));
        Assert.assertTrue(constImplicitParams.containsKey("v2"));
        Assert.assertEquals("5.41", constImplicitParams.get("v"));
        Assert.assertEquals("-alpha", constImplicitParams.get("v2"));
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

    @RequestMethod("POST")
    private interface MethodTestInterface {
        String defaultPostMethod();

        @RequestMethod("GET")
        String overrideMethod();
    }

    private interface ImplicitTestInterface {
        @ImplicitParam(paramName = "v", constValue = "5.41")
        List testMethod1(@Param("owner_id") Long ownerId);

        @ImplicitParam(paramName = "access_token", providerName = "accessTokenProvider")
        List testMethod2(@Param("owner_id") Long ownerId);

        @ImplicitParams({
                @ImplicitParam(paramName = "v", constValue = "5.41"),
                @ImplicitParam(paramName = "access_token", providerName = "accessTokenProvider")
        })
        List testMethod3(@Param("owner_id") Long ownerId);
    }

    @ImplicitParam(paramName = "v", constValue = "5.41")
    private interface ClassLevelImplicitTestInterface {
        void testMethod();
    }

    @ImplicitParam(paramName = "access_token", providerName = "accessTokenProvider")
    private interface ClassLevelProvidedImplicitTestInterface {
        void testMethod();
    }

    @ImplicitParams({
        @ImplicitParam(paramName = "v", constValue = "5.41"),
        @ImplicitParam(paramName = "access_token", providerName = "accessTokenProvider")
    })
    private interface ClassLevelInplicitParamsTestInterface { }

    @ImplicitParam(paramName = "v", constValue = "5.41")
    private interface MixedImplicitParamsTestInterface {
        @ImplicitParam(paramName = "v2", constValue = "-alpha")
        void testMethod();
    }
}