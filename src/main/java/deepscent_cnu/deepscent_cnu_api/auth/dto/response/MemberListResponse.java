package deepscent_cnu.deepscent_cnu_api.auth.dto.response;

import java.util.List;

public record MemberListResponse(
    List<MemberInfo> members
) {

}
