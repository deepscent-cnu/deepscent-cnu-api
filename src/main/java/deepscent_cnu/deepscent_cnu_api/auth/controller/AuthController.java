package deepscent_cnu.deepscent_cnu_api.auth.controller;

import deepscent_cnu.deepscent_cnu_api.auth.dto.LoginRequest;
import deepscent_cnu.deepscent_cnu_api.auth.dto.MemberResponse;
import deepscent_cnu.deepscent_cnu_api.auth.dto.SignupRequest;
import deepscent_cnu.deepscent_cnu_api.auth.service.MemberService;
import deepscent_cnu.deepscent_cnu_api.exception.ErrorCode;
import deepscent_cnu.deepscent_cnu_api.util.ApiResponse;
import deepscent_cnu.deepscent_cnu_api.util.JwtTokenProvider;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final MemberService memberService;
  private final JwtTokenProvider jwtTokenProvider;

  public AuthController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
    this.memberService = memberService;
    this.jwtTokenProvider = jwtTokenProvider;
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
  public ResponseEntity<ApiResponse<Object>> withdraw(@RequestHeader("Authorization") String authHeader) {
    if (authHeader == null || authHeader.isEmpty()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, ErrorCode.TOKEN_REQUIRED.message(), null));
    }

    // Authorization 헤더: "Bearer <token>"
    String token = authHeader.startsWith("Bearer ") ? authHeader.substring(7) : authHeader;

    if (!jwtTokenProvider.validateToken(token)) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(new ApiResponse<>(false, ErrorCode.INVALID_TOKEN.message(), null));
    }

    String username = jwtTokenProvider.getUsername(token);
    memberService.deleteMember(username);
    return ResponseEntity.ok(new ApiResponse<>(true, "회원 탈퇴가 완료되었습니다.", null));
  }
}
