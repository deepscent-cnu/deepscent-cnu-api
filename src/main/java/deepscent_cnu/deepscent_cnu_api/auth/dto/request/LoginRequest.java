package deepscent_cnu.deepscent_cnu_api.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    String phoneNumber,

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    String password
) {

}
