package deepscent_cnu.deepscent_cnu_api.auth.service;

import deepscent_cnu.deepscent_cnu_api.auth.dto.request.LoginRequest;
import deepscent_cnu.deepscent_cnu_api.auth.dto.request.SignupRequest;
import deepscent_cnu.deepscent_cnu_api.auth.dto.response.MemberInfo;
import deepscent_cnu.deepscent_cnu_api.auth.dto.response.MemberListResponse;
import deepscent_cnu.deepscent_cnu_api.auth.dto.response.MemberResponse;
import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.repository.MemberRepository;
import deepscent_cnu.deepscent_cnu_api.device_info.entity.DeviceInfo;
import deepscent_cnu.deepscent_cnu_api.device_info.repository.DeviceRegisterRepository;
import deepscent_cnu.deepscent_cnu_api.exception.ErrorCode;
import deepscent_cnu.deepscent_cnu_api.exception.MemberException;
import deepscent_cnu.deepscent_cnu_api.util.JwtTokenProvider;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final DeviceRegisterRepository deviceRegisterRepository;

  public MemberService(
      MemberRepository memberRepository,
      DeviceRegisterRepository deviceRegisterRepository,
      PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider
  ) {
    this.memberRepository = memberRepository;
    this.deviceRegisterRepository = deviceRegisterRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
  }

  @Transactional
  public MemberResponse signup(SignupRequest request) {
    if (memberRepository.findByUsername(request.username()).isPresent()) {
      throw new MemberException(ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    Member member = new Member(
        null,
        request.name(),
        request.birthDate(),
        request.phoneNumber(),
        request.username(),
        passwordEncoder.encode(request.password())
    );
    Member savedMember = memberRepository.save(member);

    deviceRegisterRepository.save(new DeviceInfo(member));

    String token = jwtTokenProvider.createToken(savedMember.getId());
    return new MemberResponse(
        savedMember.getId(),
        savedMember.getName(),
        savedMember.getBirthDate(),
        savedMember.getPhoneNumber(),
        savedMember.getUsername(),
        token
    );
  }

  @Transactional
  public MemberResponse login(LoginRequest request) {
    Member member = memberRepository.findByUsername(request.username())
        .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));

    if (!passwordEncoder.matches(request.password(), member.getPassword())) {
      throw new MemberException(ErrorCode.INVALID_PASSWORD);
    }

    String token = jwtTokenProvider.createToken(member.getId());
    return new MemberResponse(
        member.getId(),
        member.getName(),
        member.getBirthDate(),
        member.getPhoneNumber(),
        member.getUsername(),
        token
    );
  }

  @Transactional
  public void deleteMember(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));
    memberRepository.delete(member);
  }

  @Transactional(readOnly = true)
  public Member findById(Long id) {
    return memberRepository.findById(id)
        .orElseThrow(() -> new MemberException(ErrorCode.USER_NOT_FOUND));
  }

  @Transactional(readOnly = true)
  public MemberListResponse getMemberAll() {
    List<MemberInfo> memberList = memberRepository.findAll().stream()
        .map((member) -> new MemberInfo(
            member.getId(), member.getName(), member.getBirthDate(), member.getPhoneNumber(),
            member.getUsername())).toList();

    return new MemberListResponse(memberList);
  }
}
