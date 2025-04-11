package deepscent_cnu.deepscent_cnu_api.auth.service;

import deepscent_cnu.deepscent_cnu_api.auth.dto.LoginRequest;
import deepscent_cnu.deepscent_cnu_api.auth.dto.MemberResponse;
import deepscent_cnu.deepscent_cnu_api.auth.dto.SignupRequest;
import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.repository.MemberRepository;
import deepscent_cnu.deepscent_cnu_api.exception.ErrorCode;
import deepscent_cnu.deepscent_cnu_api.exception.MemberException;
import deepscent_cnu.deepscent_cnu_api.util.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;

  public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider) {
    this.memberRepository = memberRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  public MemberResponse signup(SignupRequest request) {
    if (memberRepository.findByUsername(request.username()).isPresent()) {
      throw new MemberException(ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    Member member = new Member(null, request.name(), request.birthDate(), request.phoneNumber(),
        request.username(), passwordEncoder.encode(request.password()));
    Member savedMember = memberRepository.save(member);

    String token = jwtTokenProvider.createToken(savedMember.getUsername());
    return new MemberResponse(savedMember.getId(), savedMember.getName(),
        savedMember.getBirthDate(), savedMember.getPhoneNumber(), savedMember.getUsername(), token);
  }

  public MemberResponse login(LoginRequest request) {
    Member member = memberRepository.findByUsername(request.username())
        .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), member.getPassword())) {
      throw new MemberException(ErrorCode.INVALID_PASSWORD);
    }

    String token = jwtTokenProvider.createToken(member.getUsername());
    return new MemberResponse(member.getId(), member.getName(), member.getBirthDate(),
        member.getPhoneNumber(), member.getUsername(), token);
  }
}
