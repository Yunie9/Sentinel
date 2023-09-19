/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.csp.sentinel.dashboard.config;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.dashboard.rule.apollo.ApolloRuleCenter;
import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author SZW
 */
@Configuration
@EnableConfigurationProperties(ApolloRuleCenterProperties.class)
public class ApolloRuleCenterConfiguration {
    // Apollo key postfix，e.g. ${appName}-flow-rules
    public static final String FLOW_RULES_KEY_POSTFIX = "-flow-rules";
    public static final String GW_FLOW_RULES_KEY_POSTFIX = "-gw-flow-rules";
    public static final String GW_API_DEFINITIONS_KEY_POSTFIX = "-api-definitions";
    public static final String SYSTEM_RULES_KEY_POSTFIX = "-system-rules";
    public static final String DEGRADE_RULES_KEY_POSTFIX = "-degrade-rules";
    public static final String PARAM_FLOW_RULES_KEY_POSTFIX = "-param-flow-rules";
    public static final String AUTHORITY_RULES_KEY_POSTFIX = "-authority-rules";

    private final ApolloRuleCenterProperties properties;

    public ApolloRuleCenterConfiguration(ApolloRuleCenterProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ApolloOpenApiClient apolloOpenApiClient() {
        return ApolloOpenApiClient.newBuilder()
                .withPortalUrl(properties.getPortalUrl())
                .withToken(properties.getToken())
                .withConnectTimeout(properties.getConnectTimeout())
                .withReadTimeout(properties.getReadTimeout())
                .build();
    }

    private <T extends RuleEntity> ApolloRuleCenter<List<T>> buildListRuleCenter(String keyPostfix, Class<T> clazz) {
        return ApolloRuleCenter.<List<T>>builder()
                .appId(properties.getAppId())
                .env(properties.getEnv())
                .clusterName(properties.getClusterName())
                .namespaceName(properties.getNamespaceName())
                .emergencyPublish(properties.isEmergencyPublish())
                .releaseComment(properties.getReleaseComment())
                .releasedBy(properties.getReleasedBy())
                .releaseTitle(properties.getReleaseTitle())
                .apolloOpenApiClient(apolloOpenApiClient())
                .dataIdBuilder(appName -> appName + keyPostfix)
                .serializer(JSON::toJSONString)
                .deserializer(s -> JSON.parseArray(s, clazz))
                .build();
    }

    @Bean
    public ApolloRuleCenter<List<FlowRuleEntity>> flowRuleProviderPublisher() {
        return buildListRuleCenter(FLOW_RULES_KEY_POSTFIX, FlowRuleEntity.class);
    }

    @Bean
    public ApolloRuleCenter<List<GatewayFlowRuleEntity>> gwFlowRuleProviderPublisher() {
        return buildListRuleCenter(GW_FLOW_RULES_KEY_POSTFIX, GatewayFlowRuleEntity.class);
    }

    @Bean
    public ApolloRuleCenter<List<ApiDefinitionEntity>> gwApiDefinitionProviderPublisher() {
        return buildListRuleCenter(GW_API_DEFINITIONS_KEY_POSTFIX, ApiDefinitionEntity.class);
    }

    @Bean
    public ApolloRuleCenter<List<SystemRuleEntity>> systemRuleProviderPublisher() {
        return buildListRuleCenter(SYSTEM_RULES_KEY_POSTFIX, SystemRuleEntity.class);
    }

    @Bean
    public ApolloRuleCenter<List<DegradeRuleEntity>> degradeRuleProviderPublisher() {
        return buildListRuleCenter(DEGRADE_RULES_KEY_POSTFIX, DegradeRuleEntity.class);
    }

    @Bean
    public ApolloRuleCenter<List<ParamFlowRuleEntity>> paramFlowRuleProviderPublisher() {
        return buildListRuleCenter(PARAM_FLOW_RULES_KEY_POSTFIX, ParamFlowRuleEntity.class);
    }

    @Bean
    public ApolloRuleCenter<List<AuthorityRuleEntity>> authorityRuleProviderPublisher() {
        return buildListRuleCenter(AUTHORITY_RULES_KEY_POSTFIX, AuthorityRuleEntity.class);
    }
}
