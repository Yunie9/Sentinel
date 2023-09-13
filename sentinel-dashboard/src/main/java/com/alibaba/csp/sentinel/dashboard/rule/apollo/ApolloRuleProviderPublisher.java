package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.function.Function;

/**
 * @author hantianwei@gmail.com
 * @since 1.5.0
 */
public class ApolloRuleProviderPublisher<T> implements DynamicRuleProvider<T>, DynamicRulePublisher<T> {

    private final ApolloApiClient apolloApiClient;
    private final Function<String, String> dataIdBuilder;
    private final Converter<String, T> deserializer;
    private final Converter<T, String> serializer;

    public ApolloRuleProviderPublisher(ApolloApiClient apolloApiClient, Function<String, String> dataIdBuilder,
                                       Converter<String, T> deserializer, Converter<T, String> serializer) {
        AssertUtil.notNull(apolloApiClient, "apolloApiClient cannot be null");
        AssertUtil.notNull(dataIdBuilder, "dataIdBuilder cannot be null");
        AssertUtil.notNull(deserializer, "deserializer cannot be null");
        AssertUtil.notNull(serializer, "serializer cannot be null");
        this.apolloApiClient = apolloApiClient;
        this.dataIdBuilder = dataIdBuilder;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }


    @Override
    public T getRules(String appName) throws Exception {
        AssertUtil.notEmpty(appName, "app name cannot be empty");
        String dataId = dataIdBuilder.apply(appName);
        if (dataId == null) {
            throw new IllegalArgumentException("dataIdBuilder return null");
        }
        String rules = apolloApiClient.getValue(dataId);
        return deserializer.convert(rules);
    }

    @Override
    public void publish(String appName, T rules) throws Exception {
        AssertUtil.notEmpty(appName, "app name name cannot be empty");
        if (rules == null) {
            return;
        }
        String dataId = dataIdBuilder.apply(appName);
        if (dataId == null) {
            throw new IllegalArgumentException("dataIdBuilder return null");
        }
        String rulesStr = serializer.convert(rules);
        apolloApiClient.createOrUpdateItem(dataId, rulesStr);
        apolloApiClient.release();
    }
}
