package deepscent_cnu.deepscent_cnu_api.config.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class DeepscentTokenInterceptor implements HandlerInterceptor {

  private final String AUTHORIZATION = "Authorization";

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
      throws Exception {

    String deepscentToken = request.getHeader(AUTHORIZATION);

    if (deepscentToken == null || !deepscentToken.startsWith("Bearer ")) {
      throw new Exception();
    }

    deepscentToken = deepscentToken.substring(7);
    request.setAttribute("deepscentToken", deepscentToken);

    return true;
  }
}
