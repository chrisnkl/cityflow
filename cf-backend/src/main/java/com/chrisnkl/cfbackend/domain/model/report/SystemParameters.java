package com.chrisnkl.cfbackend.domain.model.report;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class SystemParameters {

    private double defaultRadiusMeters;
    private double weightCount;
    private double weightProximity;
    private double weightSeverity;

    public static SystemParameters defaults() {
        return SystemParameters.builder()
                .defaultRadiusMeters(100.0)
                .weightCount(0.5)
                .weightProximity(0.3)
                .weightSeverity(0.2)
                .build();
    }
}
