package deepscent_cnu.deepscent_cnu_api.config.interceptor;

import deepscent_cnu.deepscent_cnu_api.util.JwtTokenProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

  private final JwtTokenProvider jwtTokenProvider;

  public InterceptorConfig(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Bean
  public DeepscentTokenInterceptor deepscentTokenInterceptor() {
    return new DeepscentTokenInterceptor();
  }

  @Bean
  public AuthTokenInterceptor authTokenInterceptor() {
    return new AuthTokenInterceptor(jwtTokenProvider);
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
//    registry.addInterceptor(authTokenInterceptor())
//        .addPathPatterns("/api/**")            // 모든 API path 에 적용
//        .excludePathPatterns("/api/auth/signup", "/api/auth/login");  // 회원가입 및 로그인 엔드포인트 제외
    registry.addInterceptor(authTokenInterceptor())
        .addPathPatterns("/api/auth/withdraw")
        .addPathPatterns("/api/normal-olfactory-training/log");

  }
}
