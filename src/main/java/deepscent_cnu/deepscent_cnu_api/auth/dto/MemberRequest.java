package deepscent_cnu.deepscent_cnu_api.auth.dto;

public record MemberRequest(
        String name,
        String birthDate,
        String phoneNumber,
        String username,
        String password
) {
}
