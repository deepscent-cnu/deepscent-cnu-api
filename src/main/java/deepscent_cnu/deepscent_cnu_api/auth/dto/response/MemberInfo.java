package deepscent_cnu.deepscent_cnu_api.auth.dto.response;

import java.time.LocalDate;

public record MemberInfo(
    Long id,
    String name,
    LocalDate birthDate,
    String phoneNumber,
    String userName
) {

}
