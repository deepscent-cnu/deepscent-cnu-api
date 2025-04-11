package deepscent_cnu.deepscent_cnu_api.auth.dto;

import java.time.LocalDate;

public record MemberResponse(
    Long id,
    String name,
    LocalDate birthDate,
    String phoneNumber,
    String username,
    String token
) {

}
