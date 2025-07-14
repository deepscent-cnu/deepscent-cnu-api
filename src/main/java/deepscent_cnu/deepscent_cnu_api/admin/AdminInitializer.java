package deepscent_cnu.deepscent_cnu_api.admin;

import deepscent_cnu.deepscent_cnu_api.auth.dto.SignupRequest;
import deepscent_cnu.deepscent_cnu_api.auth.service.MemberService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters.LocalDateConverter;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements ApplicationRunner {

  @Value("${admin.username}")
  private String username;
  @Value("${admin.password}")
  private String password;
  private final MemberService memberService;

  public AdminInitializer(MemberService memberService) {
    this.memberService = memberService;
  }

  @Override
  public void run(ApplicationArguments args) {
    memberService.signup(
        new SignupRequest("관리자", LocalDate.of(1990,1,1), "01012345678", username, password)
    );
  }
}
