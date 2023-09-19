package com.alibaba.csp.sentinel.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author SZW
 */
@ConfigurationProperties(prefix = "apollo.rule-center")
public class ApolloRuleCenterProperties {
    private String portalUrl;
    private String token;
    private int connectTimeout = -1;
    private int readTimeout = -1;
    private String appId;
    private String env;
    private String clusterName = "default";
    private String namespaceName = "application";
    private boolean emergencyPublish = true;
    private String releaseComment = "Modify or add configurations";
    private String releasedBy = "some-operator";
    private String releaseTitle = "Modify or add configurations";

    public String getPortalUrl() {
        return portalUrl;
    }

    public ApolloRuleCenterProperties setPortalUrl(String portalUrl) {
        this.portalUrl = portalUrl;
        return this;
    }

    public String getToken() {
        return token;
    }

    public ApolloRuleCenterProperties setToken(String token) {
        this.token = token;
        return this;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public ApolloRuleCenterProperties setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public ApolloRuleCenterProperties setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public String getAppId() {
        return appId;
    }

    public ApolloRuleCenterProperties setAppId(String appId) {
        this.appId = appId;
        return this;
    }

    public String getEnv() {
        return env;
    }

    public ApolloRuleCenterProperties setEnv(String env) {
        this.env = env;
        return this;
    }

    public String getClusterName() {
        return clusterName;
    }

    public ApolloRuleCenterProperties setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public String getNamespaceName() {
        return namespaceName;
    }

    public ApolloRuleCenterProperties setNamespaceName(String namespaceName) {
        this.namespaceName = namespaceName;
        return this;
    }

    public boolean isEmergencyPublish() {
        return emergencyPublish;
    }

    public ApolloRuleCenterProperties setEmergencyPublish(boolean emergencyPublish) {
        this.emergencyPublish = emergencyPublish;
        return this;
    }

    public String getReleaseComment() {
        return releaseComment;
    }

    public ApolloRuleCenterProperties setReleaseComment(String releaseComment) {
        this.releaseComment = releaseComment;
        return this;
    }

    public String getReleasedBy() {
        return releasedBy;
    }

    public ApolloRuleCenterProperties setReleasedBy(String releasedBy) {
        this.releasedBy = releasedBy;
        return this;
    }

    public String getReleaseTitle() {
        return releaseTitle;
    }

    public ApolloRuleCenterProperties setReleaseTitle(String releaseTitle) {
        this.releaseTitle = releaseTitle;
        return this;
    }
}
