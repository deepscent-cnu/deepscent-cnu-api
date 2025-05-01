package deepscent_cnu.deepscent_cnu_api.config.interceptor;

import deepscent_cnu.deepscent_cnu_api.exception.ErrorCode;
import deepscent_cnu.deepscent_cnu_api.exception.MemberException;
import deepscent_cnu.deepscent_cnu_api.util.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthTokenInterceptor implements HandlerInterceptor {

  private static final String AUTHORIZATION = "Authorization";
  private final JwtTokenProvider jwtTokenProvider;

  public AuthTokenInterceptor(JwtTokenProvider jwtTokenProvider) {
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    String header = request.getHeader(AUTHORIZATION);
    if (header == null || !header.startsWith("Bearer ")) {
      throw new MemberException(ErrorCode.TOKEN_REQUIRED);
    }

    String token = header.substring(7);
    if (!jwtTokenProvider.validateToken(token)) {
      throw new MemberException(ErrorCode.INVALID_TOKEN);
    }

    // 토큰에서 꺼낸 사용자 이름(또는 ID)은 request 에 담아둠
    String username = jwtTokenProvider.getUsername(token);
    request.setAttribute("memberUsername", username);
    return true;
  }
}
