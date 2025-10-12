package deepscent_cnu.deepscent_cnu_api.device_info.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record DeviceRegisterRequest(

    @Min(value = 1, message = "기기 번호는 1, 2, 3 중 하나여야 합니다.")
    @Max(value = 3, message = "기기 번호는 1, 2, 3 중 하나여야 합니다.")
    Integer deviceNumber,
    String deviceId
) {

}
