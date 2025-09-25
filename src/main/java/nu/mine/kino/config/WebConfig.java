package nu.mine.kino.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import lombok.AllArgsConstructor;
import nu.mine.kino.interceptor.MDCLoggingInterceptor;

// @Configuration
@AllArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final MDCLoggingInterceptor loggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor);
                // .addPathPatterns("/**");
    }
}
