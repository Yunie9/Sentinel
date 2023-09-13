package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import com.ctrip.framework.apollo.openapi.dto.NamespaceReleaseDTO;
import com.ctrip.framework.apollo.openapi.dto.OpenItemDTO;

/**
 * @author szw
 */
public class ApolloApiClient {
    private final String appId;
    private final String env;
    private final String clusterName;
    private final String namespaceName;
    private final boolean emergencyPublish;
    private final String releaseComment;
    private final String releasedBy;
    private final String releaseTitle;

    private final ApolloOpenApiClient apolloOpenApiClient;

    public ApolloApiClient(String appId, String env, String clusterName, String namespaceName,
                           boolean emergencyPublish, String releaseComment, String releasedBy, String releaseTitle,
                           ApolloOpenApiClient apolloOpenApiClient) {
        this.appId = appId;
        this.env = env;
        this.clusterName = clusterName;
        this.namespaceName = namespaceName;
        this.emergencyPublish = emergencyPublish;
        this.releaseComment = releaseComment;
        this.releasedBy = releasedBy;
        this.releaseTitle = releaseTitle;
        this.apolloOpenApiClient = apolloOpenApiClient;
    }

    public OpenItemDTO getItem(String key) {
        return apolloOpenApiClient.getItem(appId, env, clusterName, namespaceName, key);
    }

    public String getValue(String key) {
        OpenItemDTO item = getItem(key);
        return item == null ? "" : item.getValue();
    }

    public void createOrUpdateItem(String key, String value) {
        OpenItemDTO openItemDTO = new OpenItemDTO();
        openItemDTO.setKey(key);
        openItemDTO.setValue(value);
        openItemDTO.setComment(releaseComment);
        openItemDTO.setDataChangeCreatedBy(releasedBy);
        apolloOpenApiClient.createOrUpdateItem(appId, env, clusterName, namespaceName, openItemDTO);
    }

    public void release() {
        NamespaceReleaseDTO namespaceReleaseDTO = new NamespaceReleaseDTO();
        namespaceReleaseDTO.setEmergencyPublish(emergencyPublish);
        namespaceReleaseDTO.setReleaseComment(releaseComment);
        namespaceReleaseDTO.setReleasedBy(releasedBy);
        namespaceReleaseDTO.setReleaseTitle(releaseTitle);
        apolloOpenApiClient.publishNamespace(appId, env, clusterName, namespaceName, namespaceReleaseDTO);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String appId;
        private String env;
        private String clusterName;
        private String namespaceName;
        private boolean emergencyPublish;
        private String releaseComment;
        private String releasedBy;
        private String releaseTitle;
        private ApolloOpenApiClient apolloOpenApiClient;

        public Builder appId(String appId) {
            this.appId = appId;
            return this;
        }

        public Builder env(String env) {
            this.env = env;
            return this;
        }

        public Builder clusterName(String clusterName) {
            this.clusterName = clusterName;
            return this;
        }

        public Builder namespaceName(String namespaceName) {
            this.namespaceName = namespaceName;
            return this;
        }

        public Builder emergencyPublish(boolean emergencyPublish) {
            this.emergencyPublish = emergencyPublish;
            return this;
        }

        public Builder releaseComment(String releaseComment) {
            this.releaseComment = releaseComment;
            return this;
        }

        public Builder releasedBy(String releasedBy) {
            this.releasedBy = releasedBy;
            return this;
        }

        public Builder releaseTitle(String releaseTitle) {
            this.releaseTitle = releaseTitle;
            return this;
        }

        public Builder apolloOpenApiClient(ApolloOpenApiClient apolloOpenApiClient) {
            this.apolloOpenApiClient = apolloOpenApiClient;
            return this;
        }

        public ApolloApiClient build() {
            return new ApolloApiClient(appId, env, clusterName, namespaceName, emergencyPublish, releaseComment, releasedBy, releaseTitle, apolloOpenApiClient);
        }
    }
}
