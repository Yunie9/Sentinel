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
package com.alibaba.csp.sentinel.dashboard.rule.apollo;

import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.ApiDefinitionEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.gateway.GatewayFlowRuleEntity;
import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.*;
import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.fastjson.JSON;
import com.ctrip.framework.apollo.openapi.client.ApolloOpenApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author hantianwei@gmail.com
 * @since 1.5.0
 */
@Configuration
public class ApolloConfig {
    // Apollo key postfix，e.g. ${appName}-flow-rules
    public static final String FLOW_RULES_KEY_POSTFIX = "-flow-rules";
    public static final String GW_FLOW_RULES_KEY_POSTFIX = "-gw-flow-rules";
    public static final String GW_API_DEFINITIONS_KEY_POSTFIX = "-api-definitions";
    public static final String SYSTEM_RULES_KEY_POSTFIX = "-system-rules";
    public static final String DEGRADE_RULES_KEY_POSTFIX = "-degrade-rules";
    public static final String PARAM_FLOW_RULES_KEY_POSTFIX = "-param-flow-rules";
    public static final String AUTHORITY_RULES_KEY_POSTFIX = "-authority-rules";

    @Value("${apollo.meta}")
    private String portalUrl;
    @Value("${apollo.token}")
    private String token;
    @Value("${apollo.connectTimeout:-1}")
    private int connectTimeout;
    @Value("${apollo.readTimeout:-1}")
    private int readTimeout;
    @Value("${apollo.appId}")
    private String appId;
    @Value("${apollo.env}")
    private String env;
    @Value("${apollo.clusterName:default}")
    private String clusterName;
    @Value("${apollo.namespaceName:application}")
    private String namespaceName;
    @Value("${apollo.emergencyPublish:true}")
    private boolean emergencyPublish;
    @Value("${apollo.releaseComment:Modify or add configurations}")
    private String releaseComment;
    @Value("${apollo.releasedBy:some-operator}")
    private String releasedBy;
    @Value("${apollo.releaseTitle:Modify or add configurations}")
    private String releaseTitle;

    @Bean
    public ApolloOpenApiClient apolloOpenApiClient() {
        return ApolloOpenApiClient.newBuilder()
                .withPortalUrl(portalUrl)
                .withToken(token)
                .withConnectTimeout(connectTimeout)
                .withReadTimeout(readTimeout)
                .build();
    }

    @Bean
    public ApolloApiClient apolloApiClient() {
        return ApolloApiClient.builder()
                .appId(appId)
                .env(env)
                .clusterName(clusterName)
                .namespaceName(namespaceName)
                .emergencyPublish(emergencyPublish)
                .releaseComment(releaseComment)
                .releasedBy(releasedBy)
                .releaseTitle(releaseTitle)
                .apolloOpenApiClient(apolloOpenApiClient())
                .build();
    }

    @Bean
    public ApolloRuleProviderPublisher<List<FlowRuleEntity>> flowRuleProviderPublisher() {
        return new ApolloRuleProviderPublisher<>(
                apolloApiClient(),
                appName -> appName + FLOW_RULES_KEY_POSTFIX,
                s -> JSON.parseArray(s, FlowRuleEntity.class),
                JSON::toJSONString
        );
    }

    @Bean
    public ApolloRuleProviderPublisher<List<GatewayFlowRuleEntity>> gwFlowRuleProviderPublisher() {
        return new ApolloRuleProviderPublisher<>(
                apolloApiClient(),
                appName -> appName + GW_FLOW_RULES_KEY_POSTFIX,
                s -> JSON.parseArray(s, GatewayFlowRuleEntity.class),
                JSON::toJSONString
        );
    }

    @Bean
    public ApolloRuleProviderPublisher<List<ApiDefinitionEntity>> gwApiDefinitionProviderPublisher() {
        return new ApolloRuleProviderPublisher<>(
                apolloApiClient(),
                appName -> appName + GW_API_DEFINITIONS_KEY_POSTFIX,
                s -> JSON.parseArray(s, ApiDefinitionEntity.class),
                JSON::toJSONString
        );
    }

    @Bean
    public ApolloRuleProviderPublisher<List<SystemRuleEntity>> systemRuleProviderPublisher() {
        return new ApolloRuleProviderPublisher<>(
                apolloApiClient(),
                appName -> appName + SYSTEM_RULES_KEY_POSTFIX,
                s -> JSON.parseArray(s, SystemRuleEntity.class),
                JSON::toJSONString
        );
    }

    @Bean
    public ApolloRuleProviderPublisher<List<DegradeRuleEntity>> degradeRuleProviderPublisher() {
        return new ApolloRuleProviderPublisher<>(
                apolloApiClient(),
                appName -> appName + DEGRADE_RULES_KEY_POSTFIX,
                s -> JSON.parseArray(s, DegradeRuleEntity.class),
                JSON::toJSONString
        );
    }

    @Bean
    public ApolloRuleProviderPublisher<List<ParamFlowRuleEntity>> paramFlowRuleProviderPublisher() {
        return new ApolloRuleProviderPublisher<>(
                apolloApiClient(),
                appName -> appName + PARAM_FLOW_RULES_KEY_POSTFIX,
                s -> JSON.parseArray(s, ParamFlowRuleEntity.class),
                JSON::toJSONString
        );
    }

    @Bean
    public ApolloRuleProviderPublisher<List<AuthorityRuleEntity>> authorityRuleProviderPublisher() {
        return new ApolloRuleProviderPublisher<>(
                apolloApiClient(),
                appName -> appName + AUTHORITY_RULES_KEY_POSTFIX,
                s -> JSON.parseArray(s, AuthorityRuleEntity.class),
                JSON::toJSONString
        );
    }

}
