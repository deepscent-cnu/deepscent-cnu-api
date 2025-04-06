package deepscent_cnu.deepscent_cnu_api.fragrance.dto.external.deepscent;

public record DeviceStateResponse(
    Boolean power,
    String color,
    int bright,
    int fan1,
    int fan2,
    int fan3,
    int fan4,
    Boolean turbo,
    Interval interval

) {

  public record Interval(
      Boolean enable,
      int running_time,
      int rest_time
  ) {

  }
}
