package deepscent_cnu.deepscent_cnu_api.config.resolver;

import deepscent_cnu.deepscent_cnu_api.auth.service.MemberService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ResolverConfig implements WebMvcConfigurer {

  private final MemberService memberService;

  public ResolverConfig(MemberService memberService) {
    this.memberService = memberService;
  }

  @Bean
  public DeepscentTokenMethodArgumentResolver deepscentTokenMethodArgumentResolver() {
    return new DeepscentTokenMethodArgumentResolver();
  }

  @Bean
  public AuthTokenMethodArgumentResolver authTokenMethodArgumentResolver() {
    return new AuthTokenMethodArgumentResolver(memberService);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    // DeepScent 토큰 리졸버
    resolvers.add(deepscentTokenMethodArgumentResolver());
    // 회원 인증용 AuthToken 리졸버
    resolvers.add(authTokenMethodArgumentResolver());
  }
}
