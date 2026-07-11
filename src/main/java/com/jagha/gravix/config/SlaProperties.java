package com.jagha.gravix.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "gravix.sla")
public class SlaProperties {

    private long breachCheckIntervalMs = 60000;
    private int warningThresholdHours = 24;
    private int criticalThresholdHours = 1;

}
