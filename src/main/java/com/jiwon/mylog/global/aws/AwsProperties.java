package com.jiwon.mylog.global.aws;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("aws")
public class AwsProperties {
    private String access;
    private String secret;
    private S3Properties s3;

    @Data
    public static class S3Properties {
        private String bucket;
        private String region;
    }
}
