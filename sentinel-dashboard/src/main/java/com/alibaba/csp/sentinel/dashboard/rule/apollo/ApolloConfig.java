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

import com.alibaba.csp.sentinel.dashboard.datasource.entity.rule.FlowRuleEntity;
import com.alibaba.csp.sentinel.datasource.Converter;
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
                appName -> appName + "-flow-rules",
                s -> JSON.parseArray(s, FlowRuleEntity.class),
                JSON::toJSONString
        );
    }

  /*  @Bean
    public ApolloRuleProviderPublisher<List<FlowRuleEntity>> flowRuleProviderPublisher() {
        return new ApolloRuleProviderPublisher<>(
                apolloApiClient(),
                appName -> appName + "-flow-rules",
                s -> JSON.parseArray(s, FlowRuleEntity.class),
                JSON::toJSONString
        );
    }*/
}
