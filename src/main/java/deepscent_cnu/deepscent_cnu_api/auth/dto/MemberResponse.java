package deepscent_cnu.deepscent_cnu_api.auth.dto;

public record MemberResponse(
    Long id,
    String name,
    String birthDate,
    String phoneNumber,
    String username,
    String token
) {

}
