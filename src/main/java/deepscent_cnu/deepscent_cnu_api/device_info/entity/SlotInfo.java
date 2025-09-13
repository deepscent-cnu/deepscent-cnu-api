package deepscent_cnu.deepscent_cnu_api.device_info.entity;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
public class SlotInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(nullable = false)
  private Member member;

  @Getter
  @Column(nullable = false)
  private Integer deviceNumber;

  @Getter
  @Column(nullable = false)
  private Integer fanNumber;

  @Getter
  @Column(nullable = false)
  private String scent;

  public SlotInfo() {
  }

  public SlotInfo(Member member, Integer deviceNumber, Integer fanNumber, String scent) {
    this.member = member;
    this.deviceNumber = deviceNumber;
    this.fanNumber = fanNumber;
    this.scent = scent;
  }
}
