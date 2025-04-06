package deepscent_cnu.deepscent_cnu_api.config.interceptor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

  @Bean
  public DeepscentTokenInterceptor deepscentTokenInterceptor() {
    return new DeepscentTokenInterceptor();
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(deepscentTokenInterceptor())
        .addPathPatterns(InterceptorPath.CONTROL_FAN.getPath());
  }
}
