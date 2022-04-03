package com.marry.mhttp.config;

import com.marry.mhttp.EnableSimpleHttpClients;
import com.marry.mhttp.SimpleHttpClient;
import com.marry.mhttp.SimpleHttpClientFactoryBean;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 扫描注解中的
 * @author mal
 * @date 2022-04-01 15:18
 */
public class HttpClientsRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware {

    private Environment environment;

    private ResourceLoader resourceLoader;

    private final Class<? extends SimpleHttpClientFactoryBean> httpFactoryBean = SimpleHttpClientFactoryBean.class;

    @Override
    public void setEnvironment(Environment environment) {

        this.environment = environment;

    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;

    }

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        registerHttpClients(importingClassMetadata, registry);

    }

    private void registerHttpClients(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
        Map<String, Object> annotationAttributes = metadata.
                getAnnotationAttributes(EnableSimpleHttpClients.class.getName());

        Set<BeanDefinition> candidateComponents = new HashSet<>();
        ClassPathScanningCandidateComponentProvider scanner = getScanner();
        scanner.setResourceLoader(resourceLoader);
        scanner.addIncludeFilter(new AnnotationTypeFilter(SimpleHttpClient.class));
        Set<String> basePackages = getBasePackages(metadata);
        for (String basePackage : basePackages) {
            candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
        }

        for (BeanDefinition candidateComponent : candidateComponents) {
            if (candidateComponent instanceof AnnotatedBeanDefinition) {
                AnnotationMetadata clientMetadata = ((AnnotatedBeanDefinition) candidateComponent).getMetadata();
                Assert.isTrue(clientMetadata.isInterface(), "@SimpleHttpClient only be specified on an interface");
                Map<String, Object> attributes = clientMetadata.getAnnotationAttributes(SimpleHttpClient.class.getCanonicalName());
                String name = getClientName(attributes);
                registerHttpClient(registry, clientMetadata, attributes);

            }

        }
    }

    private void registerHttpClient(BeanDefinitionRegistry registry, AnnotationMetadata metadata, Map<String, Object> attributes) {
        String className = metadata.getClassName();
        Class<?> clazz = ClassUtils.resolveClassName(className, null);
        //ConfigurableBeanFactory beanFactory = registry instanceof ConfigurableBeanFactory ? ((ConfigurableBeanFactory) registry) : null;
        String name = getClientName(attributes);

        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(httpFactoryBean);
        beanDefinitionBuilder.addPropertyValue("name",name);
        beanDefinitionBuilder.addPropertyValue("contextId",name);
        //beanDefinitionBuilder.addPropertyValue("type",clazz);
        Object fallback = attributes.get("fallback");
        if (fallback != null) {
            beanDefinitionBuilder.addPropertyValue("fallback",fallback);
        }
        beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        beanDefinitionBuilder.setLazyInit(true);
        AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getBeanDefinition();
        beanDefinition.getConstructorArgumentValues().addGenericArgumentValue(className);

        beanDefinition.setPrimary(true);
        BeanDefinitionHolder beanDefinitionHolder = new BeanDefinitionHolder(beanDefinition, className);
        BeanDefinitionReaderUtils.registerBeanDefinition(beanDefinitionHolder, registry);


    }

    private String getClientName(Map<String, Object> attributes) {

        String value = (String) attributes.get("value");
        if (StringUtils.hasText(value)) {
            return value;
        }

        String name = (String) attributes.get("name");
        if (StringUtils.hasText(name)) {
            return name;
        }


        throw new IllegalStateException("Either 'value' or 'name' is required for @" + SimpleHttpClient.class.getSimpleName());
    }

    private Set<String> getBasePackages(AnnotationMetadata metadata) {
        Map<String, Object> annotationAttributes = metadata.
                getAnnotationAttributes(EnableSimpleHttpClients.class.getCanonicalName());
        Set<String> basePackages = new HashSet<>();

        for (String value : ((String[]) annotationAttributes.get("values"))) {
            if (StringUtils.hasText(value)) {
                basePackages.add(value);
            }
        }
        for (String value : ((String[]) annotationAttributes.get("basePackages"))) {
            if (StringUtils.hasText(value)) {
                basePackages.add(value);
            }
        }

        if (basePackages.isEmpty()) {
            basePackages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }

        return basePackages;

    }

    private ClassPathScanningCandidateComponentProvider getScanner() {
        return new ClassPathScanningCandidateComponentProvider(false, this.environment) {

            @Override
            protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
                boolean isCandidate = false;
                if (beanDefinition.getMetadata().isIndependent() && !beanDefinition.getMetadata().isAnnotation()) {
                    isCandidate = true;
                }
                return isCandidate;
            }
        };
    }
}
