package deepscent_cnu.deepscent_cnu_api.openai;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class MemoryRecallRound {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private Member member;
    private Long round;
    private LocalDateTime createdAt;
    private String scent;
    private String summary;
    private String feeling;

    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<UserChatMemory> messages = new ArrayList<>();

}
