package deepscent_cnu.deepscent_cnu_api.admin;

import deepscent_cnu.deepscent_cnu_api.auth.dto.response.MemberListResponse;
import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.service.MemberService;
import deepscent_cnu.deepscent_cnu_api.config.resolver.AdminToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

  private final MemberService memberService;

  public AdminController(MemberService memberService) {
    this.memberService = memberService;
  }

  @GetMapping("/members")
  public MemberListResponse getAllMembers(@AdminToken Member admin) {
    return memberService.getMemberAll();
  }

}
