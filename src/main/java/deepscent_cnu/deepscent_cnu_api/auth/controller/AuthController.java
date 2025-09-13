package deepscent_cnu.deepscent_cnu_api.auth.controller;

import deepscent_cnu.deepscent_cnu_api.auth.dto.request.LoginRequest;
import deepscent_cnu.deepscent_cnu_api.auth.dto.request.SignupRequest;
import deepscent_cnu.deepscent_cnu_api.auth.dto.response.MemberResponse;
import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.service.MemberService;
import deepscent_cnu.deepscent_cnu_api.config.resolver.AuthToken;
import deepscent_cnu.deepscent_cnu_api.util.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final MemberService memberService;

  public AuthController(MemberService memberService) {
    this.memberService = memberService;
  }

  @PostMapping("/signup")
  public ResponseEntity<ApiResponse<String>> signup(@Valid @RequestBody SignupRequest request) {
    MemberResponse memberResponse = memberService.signup(request);
    return ResponseEntity.ok(new ApiResponse<>(true, "회원 가입 성공", memberResponse.token()));
  }

  @PostMapping("/login")
  public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody LoginRequest request) {
    MemberResponse memberResponse = memberService.login(request);
    return ResponseEntity.ok(new ApiResponse<>(true, "로그인 성공", memberResponse.token()));
  }

  @DeleteMapping("/withdraw")
  public ResponseEntity<ApiResponse<Object>> withdraw(@AuthToken Member member) {
    memberService.deleteMember(member.getId());
    return ResponseEntity.ok(new ApiResponse<>(true, "회원 탈퇴가 완료되었습니다.", null));
  }
}
