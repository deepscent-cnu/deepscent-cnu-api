package deepscent_cnu.deepscent_cnu_api.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record SignupRequest(
        @NotBlank(message = "이름은 필수 입력 항목입니다.")
        String name,

        @NotBlank(message = "생년월일은 필수 입력 항목입니다.")
        String birthDate,

        @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
        String phoneNumber,

        @NotBlank(message = "아이디는 필수 입력 항목입니다.")
        String username,

        @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
        String password
) {
}
