package org.m2sec.common.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author: outlaws-bai
 * @date: 2024/6/21 20:23
 * @description:
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
public class CloudConfig {

    private AwsConfig awsConfig;

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public static class AwsConfig {
        private String service;
        private String ak;
        private String sk;
        private String token;
        private String region;
    }
}
