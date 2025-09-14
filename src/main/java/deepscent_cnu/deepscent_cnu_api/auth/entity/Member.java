package deepscent_cnu.deepscent_cnu_api.auth.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;

@Entity
@Getter  // 전체 필드의 getter 생성
public class Member {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private LocalDate birthDate;  // e.g. 2025-04-11

  @Column(unique = true, nullable = false, length = 11)
  private String phoneNumber;  // e.g. 01012341234 (하이픈 미포함, 11자리), 아이디 겸용

  @Column(nullable = false)
  private String password;  // 해시된 비밀번호

  public Member() {

  }

  public Member(
      Long id,
      String name,
      LocalDate birthDate,
      String phoneNumber,
      String password
  ) {
    this.id = id;
    this.name = name;
    this.birthDate = birthDate;
    this.phoneNumber = phoneNumber;
    this.password = password;
  }

}
