package deepscent_cnu.deepscent_cnu_api.openai;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.repository.MemberRepository;
import deepscent_cnu.deepscent_cnu_api.config.resolver.AuthToken;
import deepscent_cnu.deepscent_cnu_api.openai.dto.response.LastRoundResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;
  private final MemoryRecallRoundRepository memoryRecallRoundRepository;
  private final MemberRepository memberRepository;
  private final UserChatMemoryRepository userChatMemoryRepository;

  //  @PostMapping("/api/chat/{userId}")
//  public String chat(@PathVariable Integer userId, @RequestBody ChatRequest request) {
//    return chatService.chat(userId, request.getUserMessage());
//  }
//
//    //chat gpt와 대화
//    @PostMapping("/api/chat1/{userId}")
//    public String chat1(@PathVariable Long userId, @RequestBody ChatRequest1 request) {
//        return chatService.chat(userId, request.getUserMessage());
//    }

  //해당 회차 대화 시작
  //회차 시작하고 향기를 선택한 후 호출
  @PostMapping("/api/chat/{userId}/{roundId}")
  public MemoryRecallRound startChat(@PathVariable(name = "userId") Long userId,
      @PathVariable(name = "roundId") Long roundId,
      @RequestParam(name = "scent") String scent) {

    Optional<Member> member = memberRepository.findById(userId);
    MemoryRecallRound byMemberAndAndRound = memoryRecallRoundRepository.findByMemberAndAndRound(
        member.get(), roundId);
    if (byMemberAndAndRound != null) {
      // 자식만 삭제
      userChatMemoryRepository.deleteByMemoryRecallRound(byMemberAndAndRound);
      return byMemberAndAndRound;

    } else {
      MemoryRecallRound memoryRecallRound1 = new MemoryRecallRound();
      memoryRecallRound1.setMember(member.get());
      memoryRecallRound1.setRound(roundId);
      memoryRecallRound1.setCreatedAt(java.time.LocalDateTime.now());
      memoryRecallRound1.setScent(scent);
      memoryRecallRoundRepository.save(memoryRecallRound1);
      return memoryRecallRound1;
    }
  }

  //해당 회차 대화 내용 저장
  @PostMapping("/api/chat1/{roundId}")
  public String chat2(@PathVariable(name = "roundId") Long roundId,
      @RequestBody ChatRequest1 request) {
    // 회차 id 저
    return chatService.chat(roundId, request.getUserMessage());
  }

  @GetMapping("/api/chat/last-round")
  public ResponseEntity<LastRoundResponse> getRoundList(@AuthToken Member member) {
    return ResponseEntity.ok().body(chatService.getRoundList(member));
  }
}
