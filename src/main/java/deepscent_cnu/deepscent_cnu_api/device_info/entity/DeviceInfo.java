package deepscent_cnu.deepscent_cnu_api.device_info.entity;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class DeviceInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne
  @JoinColumn(nullable = false)
  private Member member;

  private String deviceId1;
  private String deviceId2;
  private String deviceId3;

  public DeviceInfo() {
  }

  public DeviceInfo(Member member) {
    this.member = member;
  }

  public String getDeviceId1() {
    return deviceId1;
  }

  public String getDeviceId2() {
    return deviceId2;
  }

  public String getDeviceId3() {
    return deviceId3;
  }

  public DeviceInfo getRegistered(Integer deviceNumber, String deviceId) {
    switch (deviceNumber) {
      case 1 -> this.deviceId1 = deviceId;
      case 2 -> this.deviceId2 = deviceId;
      case 3 -> this.deviceId3 = deviceId;
      default -> throw new IllegalArgumentException("올바른 기기 번호가 아닙니다.");
    }

    return this;
  }
}
