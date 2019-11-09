package com.king.staff.common.config;

import com.king.staff.common.aop.SentryClientAspect;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * use this common config for web app
 */
@Configuration
@Import({StaffjoyConfig.class, SentryClientAspect.class})
public class StaffjoyWebConfig {
}
