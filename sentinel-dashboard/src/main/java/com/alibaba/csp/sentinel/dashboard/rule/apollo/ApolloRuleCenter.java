package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.dashboard.rule.DynamicRuleProvider;
import com.alibaba.csp.sentinel.dashboard.rule.DynamicRulePublisher;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.util.AssertUtil;
import com.alibaba.csp.sentinel.util.function.Function;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;

import java.util.Date;

/**
 * @author hantianwei@gmail.com
 * @since 1.5.0
 */
public class ApolloRuleCenter<T> implements DynamicRuleProvider<T>, DynamicRulePublisher<T> {

    private final String appId;
    private final String env;
    private final String clusterName;
    private final String namespaceName;
    private final boolean emergencyPublish;
    private final String releaseComment;
    private final String releasedBy;
    private final String releaseTitle;

    private final ApolloOpenApiClient apolloOpenApiClient;

    private final Function<String, String> dataIdBuilder;
    private final Converter<String, T> deserializer;
    private final Converter<T, String> serializer;

    public ApolloRuleCenter(Builder<T> builder) {
        this.appId = builder.appId;
        this.env = builder.env;
        this.clusterName = builder.clusterName;
        this.namespaceName = builder.namespaceName;
        this.emergencyPublish = builder.emergencyPublish;
        this.releaseComment = builder.releaseComment;
        this.releasedBy = builder.releasedBy;
        this.releaseTitle = builder.releaseTitle;
        this.apolloOpenApiClient = builder.apolloOpenApiClient;
        this.dataIdBuilder = builder.dataIdBuilder;
        this.deserializer = builder.deserializer;
        this.serializer = builder.serializer;
    }

    public static <T> Builder<T> builder() {
        return new Builder<>();
    }


    @Override
    public T getRules(String appName) throws Exception {
        AssertUtil.notEmpty(appName, "app name cannot be empty");
        String dataId = dataIdBuilder.apply(appName);
        if (dataId == null) {
            throw new IllegalArgumentException("dataIdBuilder return null");
        }
        String rules = getValue(dataId);
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
        createOrUpdateItem(dataId, rulesStr);
        release();
    }

    protected OpenItemDTO getItem(String key) {
        return apolloOpenApiClient.getItem(appId, env, clusterName, namespaceName, key);
    }

    protected String getValue(String key) {
        OpenItemDTO item = getItem(key);
        return item == null ? "" : item.getValue();
    }

    protected void createOrUpdateItem(String key, String value) {
        Date currentDate = new Date();
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(key);
        openItemDTO.setValue(value);
        openItemDTO.setComment(releaseComment);
        openItemDTO.setDataChangeCreatedBy(releasedBy);
        openItemDTO.setDataChangeCreatedTime(currentDate);
        openItemDTO.setDataChangeLastModifiedBy(releasedBy);
        openItemDTO.setDataChangeLastModifiedTime(currentDate);
        apolloOpenApiClient.createOrUpdateItem(appId, env, clusterName, namespaceName, openItemDTO);
    }

    protected void release() {
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(emergencyPublish);
        namespaceReleaseDTO.setReleaseComment(releaseComment);
        namespaceReleaseDTO.setReleasedBy(releasedBy);
        namespaceReleaseDTO.setReleaseTitle(releaseTitle);
        apolloOpenApiClient.publishNamespace(appId, env, clusterName, namespaceName, namespaceReleaseDTO);
    }

    public static class Builder<T> {
        private String appId;
        private String env;
        private String clusterName;
        private String namespaceName;
        private boolean emergencyPublish;
        private String releaseComment;
        private String releasedBy;
        private String releaseTitle;

        private ApolloOpenApiClient apolloOpenApiClient;

        private Function<String, String> dataIdBuilder;
        private Converter<String, T> deserializer;
        private Converter<T, String> serializer;

        public Builder<T> appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder<T> env(String env) {
            this.env = env;
            return this;
        }

        public Builder<T> clusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        public Builder<T> namespaceName(String namespaceName) {
            this.namespaceName = namespaceName;
            return this;
        }

        public Builder<T> emergencyPublish(boolean emergencyPublish) {
            this.emergencyPublish = emergencyPublish;
            return this;
        }

        public Builder<T> releaseComment(String releaseComment) {
            this.releaseComment = releaseComment;
            return this;
        }

        public Builder<T> releasedBy(String releasedBy) {
            this.releasedBy = releasedBy;
            return this;
        }

        public Builder<T> releaseTitle(String releaseTitle) {
            this.releaseTitle = releaseTitle;
            return this;
        }

        public Builder<T> apolloOpenApiClient(ApolloOpenApiClient apolloOpenApiClient) {
            this.apolloOpenApiClient = apolloOpenApiClient;
            return this;
        }

        public Builder<T> dataIdBuilder(Function<String, String> dataIdBuilder) {
            this.dataIdBuilder = dataIdBuilder;
            return this;
        }

        public Builder<T> deserializer(Converter<String, T> deserializer) {
            this.deserializer = deserializer;
            return this;
        }

        public Builder<T> serializer(Converter<T, String> serializer) {
            this.serializer = serializer;
            return this;
        }

        public ApolloRuleCenter<T> build() {
            AssertUtil.notEmpty(appId, "appId cannot be empty");
            AssertUtil.notEmpty(env, "env cannot be empty");
            AssertUtil.notEmpty(clusterName, "clusterName cannot be empty");
            AssertUtil.notEmpty(namespaceName, "namespaceName cannot be empty");
            AssertUtil.notNull(apolloOpenApiClient, "apolloOpenApiClient cannot be null");
            AssertUtil.notNull(dataIdBuilder, "dataIdBuilder cannot be null");
            AssertUtil.notNull(deserializer, "deserializer cannot be null");
            AssertUtil.notNull(serializer, "serializer cannot be null");
            return new ApolloRuleCenter<>(this);
        }
    }
}
