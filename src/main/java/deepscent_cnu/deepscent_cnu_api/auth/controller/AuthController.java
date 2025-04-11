package deepscent_cnu.deepscent_cnu_api.auth.controller;

import deepscent_cnu.deepscent_cnu_api.auth.dto.MemberRequest;
import deepscent_cnu.deepscent_cnu_api.auth.dto.MemberResponse;
import deepscent_cnu.deepscent_cnu_api.auth.service.MemberService;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<String> signup(@RequestBody MemberRequest request) {
        MemberResponse memberResponse = memberService.signup(request);
        return ResponseEntity.ok(memberResponse.token()); // 프론트에서 이 토큰 저장해서 사용
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody MemberRequest request) {
        MemberResponse memberResponse = memberService.login(request);
        return ResponseEntity.ok(memberResponse.token()); // 프론트에서 이 토큰 저장해서 사용
    }
}
