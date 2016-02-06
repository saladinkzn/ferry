package ru.shadam.ferry.factory;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import ru.shadam.ferry.factory.executor.MethodExecutor;
import ru.shadam.ferry.implicit.ImplicitParameterProvider;
import ru.shadam.ferry.implicit.ImplicitParameterWithNameProvider;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author sala
 */
public class MethodInvocationHandlerTest {
    @Test
    public void testSimpleParams() throws Throwable {
        final Method testMethod = TestInterface.class.getMethod("testMethod");
        final MethodExecutor methodExecutor = Mockito.mock(MethodExecutor.class);
        final MethodInvocationHandler methodInvocationHandler = new MethodInvocationHandler(ImmutableMap.<Method, MethodInvocationHandler.MethodExecutionContext<?>>of(
                testMethod,
                new MethodInvocationHandler.MethodExecutionContext<>(
                        methodExecutor,
                        ImmutableMap.of(0, "param1", 1, "param2"),
                        Maps.<String, String>newHashMap(),
                        Maps.<String, String>newHashMap()
                )
        ), ImmutableMap.<String, ImplicitParameterProvider>of());
        final Object result = methodInvocationHandler.invoke(null, testMethod, new Object[]{"value1", "value2"});
        final ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(methodExecutor, Mockito.only()).execute(mapArgumentCaptor.capture());
        final Map value = mapArgumentCaptor.getValue();
        Assert.assertEquals("value1", value.get("param1"));
        Assert.assertEquals("value2", value.get("param2"));
    }

    @Test
    public void testConstImplicitParams() throws Throwable {
        final Method method = TestInterface.class.getMethod("testMethod");
        final MethodExecutor methodExecutor = Mockito.mock(MethodExecutor.class);
        final MethodInvocationHandler methodInvocationHandler = new MethodInvocationHandler(
                ImmutableMap.<Method, MethodInvocationHandler.MethodExecutionContext<?>>of(
                        method,
                        new MethodInvocationHandler.MethodExecutionContext<>(
                                methodExecutor,
                                Maps.<Integer, String>newHashMap(),
                                ImmutableMap.of("constParam", "constValue"),
                                ImmutableMap.<String, String>of()
                        )
                ), ImmutableMap.<String, ImplicitParameterProvider>of());
        final Object result = methodInvocationHandler.invoke(null, method, new Object[0]);
        final ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(methodExecutor, Mockito.only()).execute(mapArgumentCaptor.capture());
        final Map paramsMap = mapArgumentCaptor.getValue();
        Assert.assertEquals("constValue", paramsMap.get("constParam"));
    }

    @Test
    public void testProvidedImplicitParam() throws Throwable {
        final Method method = TestInterface.class.getMethod("testMethod");
        final MethodExecutor methodExecutor = Mockito.mock(MethodExecutor.class);
        final MethodInvocationHandler methodInvocationHandler = new MethodInvocationHandler(
                ImmutableMap.<Method, MethodInvocationHandler.MethodExecutionContext<?>>of(
                        method,
                        new MethodInvocationHandler.MethodExecutionContext<>(
                                methodExecutor,
                                ImmutableMap.<Integer, String>of(),
                                ImmutableMap.<String, String>of(),
                                ImmutableMap.of("param1", "providedName")
                        )
                ), ImmutableMap.<String, ImplicitParameterProvider>of("providedName", new ImplicitParameterProvider() {
                    @Override
                    public String provideValue() {
                        return "providedValue";
                    }
                })
        );
        final Object result = methodInvocationHandler.invoke(null, method, new Object[0]);
        final ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(methodExecutor, Mockito.only()).execute(mapArgumentCaptor.capture());
        final Map value = mapArgumentCaptor.getValue();
        Assert.assertEquals("providedValue", value.get("param1"));
    }

    @Test
    public void testProvidedImplicitParam2() throws Throwable {
        final Method method = TestInterface.class.getMethod("testMethod");
        final MethodExecutor methodExecutor = Mockito.mock(MethodExecutor.class);
        final MethodInvocationHandler methodInvocationHandler = new MethodInvocationHandler(
                ImmutableMap.<Method, MethodInvocationHandler.MethodExecutionContext<?>>of(
                        method,
                        new MethodInvocationHandler.MethodExecutionContext<>(
                                methodExecutor,
                                ImmutableMap.<Integer, String>of(),
                                ImmutableMap.<String, String>of(),
                                ImmutableMap.of("", "providedName")
                        )
                ), ImmutableMap.<String, ImplicitParameterWithNameProvider>of("providedName", new ImplicitParameterWithNameProvider() {
            @Override
            public String provideValue() {
                return "providedValue";
            }

            @Override
            public String parameterName() {
                return "param1";
            }
        })
        );
        final Object result = methodInvocationHandler.invoke(null, method, new Object[0]);
        final ArgumentCaptor<Map> mapArgumentCaptor = ArgumentCaptor.forClass(Map.class);
        Mockito.verify(methodExecutor, Mockito.only()).execute(mapArgumentCaptor.capture());
        final Map value = mapArgumentCaptor.getValue();
        Assert.assertEquals("providedValue", value.get("param1"));
    }

    private interface TestInterface {
        void testMethod();
    }
}
