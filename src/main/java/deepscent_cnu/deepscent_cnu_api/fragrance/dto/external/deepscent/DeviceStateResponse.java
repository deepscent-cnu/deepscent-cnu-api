package deepscent_cnu.deepscent_cnu_api.fragrance.dto.external.deepscent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DeviceStateResponse(
    CartridgeInfo cart1,
    CartridgeInfo cart2,
    CartridgeInfo cart3,
    CartridgeInfo cart4
) {

  public record CartridgeInfo(
      String serial,
      int remain,
      int time
  ) {

  }
}
