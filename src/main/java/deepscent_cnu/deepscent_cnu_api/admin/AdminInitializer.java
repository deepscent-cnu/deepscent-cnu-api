package deepscent_cnu.deepscent_cnu_api.admin;

import deepscent_cnu.deepscent_cnu_api.auth.dto.request.SignupRequest;
import deepscent_cnu.deepscent_cnu_api.auth.dto.response.MemberResponse;
import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.repository.MemberRepository;
import deepscent_cnu.deepscent_cnu_api.auth.service.MemberService;
import deepscent_cnu.deepscent_cnu_api.device_info.dto.request.DeviceRegisterRequest;
import deepscent_cnu.deepscent_cnu_api.device_info.service.DeviceRegisterService;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

  private static final List<String> deviceIds = List.of("lounge_08D1F918C7EC",
      "lounge_08D1F918C7EC", "lounge_08D1F918C7EC");
  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final DeviceRegisterService deviceRegisterService;
  @Value("${admin.username}")
  private String username;
  @Value("${admin.password}")
  private String password;

  public AdminInitializer(MemberService memberService, MemberRepository memberRepository,
      DeviceRegisterService deviceRegisterService) {
    this.memberService = memberService;
    this.deviceRegisterService = deviceRegisterService;
    this.memberRepository = memberRepository;
  }

  @Override
  public void run(ApplicationArguments args) {
    MemberResponse memberResponse = memberService.signup(
        new SignupRequest("관리자", LocalDate.of(1990, 1, 1), "01012345678", username, password)
    );
    Member member = memberRepository.findById(memberResponse.id())
        .orElseThrow(() -> new NoSuchElementException("유저 정보가 존재하지 않습니다."));

    for (int deviceNumber = 1; deviceNumber <= 3; deviceNumber++) {
      deviceRegisterService.registerDeviceId(member,
          new DeviceRegisterRequest(deviceNumber, deviceIds.get(deviceNumber - 1)));
    }
  }
}
