package com.king.staff.common.services;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

    private int security;//public ,authenticated,or admin

    private boolean restrictDev;//if true,service is suppressed in stage and prod

    private String backendDomain;//backend service to query

    private boolean noCacheHtml;//if true,injects a header for HTML responses telling the browser not to cache HTML
}
