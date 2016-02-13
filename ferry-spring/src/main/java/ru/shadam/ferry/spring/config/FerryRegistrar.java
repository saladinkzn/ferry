package ru.shadam.ferry.spring.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import ru.shadam.ferry.simple.DefaultClientImplementationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sala
 */
public class FerryRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {
    //
    private Environment environment;
    private ResourceLoader resourceLoader;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata annotationMetadata, BeanDefinitionRegistry registry) {
        Assert.notNull(annotationMetadata);
        Assert.notNull(registry);
        Assert.notNull(resourceLoader);

        if(!registry.containsBeanDefinition("clientImplFactory")) {
            if (!registry.containsBeanDefinition("objectMapper")) {
                final AbstractBeanDefinition objectMapperBeanDefinition = BeanDefinitionBuilder
                        .rootBeanDefinition(ObjectMapper.class)
                        .getRawBeanDefinition();
                registry.registerBeanDefinition("objectMapper", objectMapperBeanDefinition);
            }

            if (!registry.containsBeanDefinition("httpClient")) {
                final AbstractBeanDefinition builderBeanDefinition = BeanDefinitionBuilder
                        .rootBeanDefinition(HttpClientBuilder.class, "create")
                        .getRawBeanDefinition();
                registry.registerBeanDefinition("httpClientBuilder", builderBeanDefinition);

                final AbstractBeanDefinition clientBuildDefinition = BeanDefinitionBuilder
                        .genericBeanDefinition()
                        .setFactoryMethod("build")
                        .getRawBeanDefinition();
                clientBuildDefinition.setFactoryBeanName("httpClientBuilder");
                registry.registerBeanDefinition("httpClient", clientBuildDefinition);
            }

            registry.registerBeanDefinition("clientImplFactory", BeanDefinitionBuilder.rootBeanDefinition(DefaultClientImplementationFactory.class)
                    .addConstructorArgReference("httpClient")
                    .addConstructorArgReference("objectMapper")
                    .getRawBeanDefinition());
        }

        final Map<String, Object> annotationAttributes = annotationMetadata.getAnnotationAttributes(EnableFerries.class.getCanonicalName());
        final String[] basePackages = (String[]) annotationAttributes.get("value");

        final ClassPathScanningCandidateComponentProvider classPathScanningCandidateComponentProvider
                = new FerryCandidateProvider(environment);


        final List<BeanDefinition> candidates = new ArrayList<>();
        for(String basePackage: basePackages) {
            candidates.addAll(classPathScanningCandidateComponentProvider.findCandidateComponents(basePackage));
        }
        //
        for (BeanDefinition candidate: candidates) {
            final AbstractBeanDefinition interfaceImplementation = BeanDefinitionBuilder
                    .genericBeanDefinition(candidate.getBeanClassName())

                    .setFactoryMethod("getInterfaceImplementation")
                    .addConstructorArgValue(candidate.getBeanClassName())
                    .getRawBeanDefinition();
            interfaceImplementation.setFactoryBeanName("clientImplFactory");
            try {
                // TODO:
                registry.registerBeanDefinition(Class.forName(candidate.getBeanClassName()).getSimpleName(), interfaceImplementation);
            } catch (ClassNotFoundException ignored) {
            }
        }

    }
}
