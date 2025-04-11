package deepscent_cnu.deepscent_cnu_api.auth.entity;


import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter  // 전체 필드의 getter 생성
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String birthDate;
    private String phoneNumber;

    @Column(unique = true)
    private String username;
    private String password;  // 해시된 비밀번호

    public Member() {

    }

    public Member(Long id, String name, String birthDate, String phoneNumber, String username, String password) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.password = password;
    }

}
