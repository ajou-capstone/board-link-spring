package LinkerBell.campus_market_spring.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
public record AwsProperties(String bucket, String region, String folder) {

}
