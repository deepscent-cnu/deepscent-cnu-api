package deepscent_cnu.deepscent_cnu_api.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;

public record SignupRequest(
    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    String name,

    @Past(message = "생년월일은 과거 날짜여야 합니다.")
    @NotNull(message = "생년월일은 필수 입력 항목입니다.")
    LocalDate birthDate,

    @Pattern(regexp = "^010\\d{8}$", message = "전화번호 형식이 올바르지 않습니다.")
    String phoneNumber,

    @NotBlank(message = "아이디는 필수 입력 항목입니다.")
    String username,

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    String password
) {

}
