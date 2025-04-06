package deepscent_cnu.deepscent_cnu_api.config.resolver;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResolverConfig implements WebMvcConfigurer {

  @Bean
  public DeepscentTokenMethodArgumentResolver deepscentTokenMethodArgumentResolver() {
    return new DeepscentTokenMethodArgumentResolver();
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(deepscentTokenMethodArgumentResolver());
  }
}
