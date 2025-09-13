package deepscent_cnu.deepscent_cnu_api.config.resolver;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.service.MemberService;
import deepscent_cnu.deepscent_cnu_api.exception.ErrorCode;
import deepscent_cnu.deepscent_cnu_api.exception.MemberException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class AdminTokenMethodArgumentResolver implements HandlerMethodArgumentResolver {

  private final MemberService memberService;

  public AdminTokenMethodArgumentResolver(MemberService memberService) {
    this.memberService = memberService;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(AdminToken.class) && Member.class.isAssignableFrom(
        parameter.getParameterType());
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
    HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
    Long memberId = (Long) request.getAttribute("memberId");

    if (memberId == null) {
      throw new MemberException(ErrorCode.TOKEN_REQUIRED);
    }

    Member member = memberService.findById(memberId);

    if (!member.getUsername().equalsIgnoreCase("admin")) {
      throw new MemberException(ErrorCode.NOT_ADMIN);
    }

    return memberService.findById(memberId);
  }
}
