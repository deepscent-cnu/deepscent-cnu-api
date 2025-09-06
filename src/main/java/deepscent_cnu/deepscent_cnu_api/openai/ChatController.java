package deepscent_cnu.deepscent_cnu_api.openai;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.auth.repository.MemberRepository;
import deepscent_cnu.deepscent_cnu_api.config.resolver.AuthToken;
import deepscent_cnu.deepscent_cnu_api.openai.dto.response.LastRoundResponse;
import java.util.List;
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
  private final LlmConversationSummarizer llmConversationSummarizer;

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
  @PostMapping("/api/chat/start/{roundId}")
  public MemoryRecallRound startChat(@AuthToken Member member,
      @PathVariable(name = "roundId") Long roundId,
      @RequestParam(name = "scent") String scent) {


    MemoryRecallRound byMemberAndAndRound = memoryRecallRoundRepository.findByMemberAndAndRound(
        member, roundId);
    if (byMemberAndAndRound != null) {
      // 자식만 삭제
      userChatMemoryRepository.deleteByMemoryRecallRound(byMemberAndAndRound);
      return byMemberAndAndRound;

    } else {
      MemoryRecallRound memoryRecallRound1 = new MemoryRecallRound();
      memoryRecallRound1.setMember(member);
      memoryRecallRound1.setRound(roundId);
      memoryRecallRound1.setCreatedAt(java.time.LocalDateTime.now());
      memoryRecallRound1.setScent(scent);
      memoryRecallRoundRepository.save(memoryRecallRound1);
      return memoryRecallRound1;
    }
  }

  //해당 회차 대화 내용 저장
  @PostMapping("/api/chat/{roundId}")
  public String chat2(@PathVariable(name = "roundId") Long roundId,
      @RequestBody ChatRequest1 request) {
    // 회차 id 저
    return chatService.chat(roundId, request.getUserMessage());
  }

  @GetMapping("/api/chat/last-round")
  public ResponseEntity<LastRoundResponse> getRoundList(@AuthToken Member member) {
    return ResponseEntity.ok().body(chatService.getRoundList(member));
  }

  //저장하기를 누르면 해당 값이 저장(여기서 roundId는 단순 회차 값이 아니라 )
  @PostMapping("/api/chat/summary/{roundId}")
  public void saveSummary(@PathVariable(name = "roundId") Long roundId) {
    MemoryRecallRound memoryRecallRound = memoryRecallRoundRepository.findById(roundId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid roundId: " + roundId));

    List<String> userOnly = userChatMemoryRepository.findUserMessagesTextByRound(memoryRecallRound);

    // 2) 하나의 텍스트로 합치기 (줄바꿈으로 구분)
    String userTranscript = String.join("\n", userOnly);

    String scent = Optional.ofNullable(memoryRecallRound.getScent())
        .filter(s -> !s.isBlank())
        .orElse("미상"); // fallback
    String summary = llmConversationSummarizer.summarizeFromRawText(userTranscript, scent);

    memoryRecallRound.setSummary(summary);
    memoryRecallRoundRepository.save(memoryRecallRound);
  }

  //완료된 회차 누르면 결과화면 데이터 불러오기
  @GetMapping("/api/chat/{roundId}")
  public MemoryRecallRound getChatHistory(@AuthToken Member member,
      @PathVariable(name = "roundId") Long roundId) {
    MemoryRecallRound byMemberAndAndRound = memoryRecallRoundRepository.findByMemberAndAndRound(
        member, roundId);
    if (byMemberAndAndRound != null) {
      return byMemberAndAndRound;
    } else {
      throw new IllegalArgumentException("Invalid userId or roundId");
    }
  }

  //느낌저장하기
  @PostMapping("/api/chat/feeling/{roundId}")
  public void saveFeeling(@PathVariable(name = "roundId") Long roundId,@AuthToken Member member,
      @RequestParam String feeling) {
    MemoryRecallRound memoryRecallRound = memoryRecallRoundRepository.findByMemberAndAndRound(member,roundId);
    memoryRecallRound.setFeeling(feeling);
    memoryRecallRoundRepository.save(memoryRecallRound);
  }
  //종료된 회차 라운드 불러오기
  @GetMapping("/api/chat/read/{roundId}")
  public MemoryRecallRound getCompletedRound(@AuthToken Member member,
      @PathVariable(name = "roundId") Long roundId) {
    MemoryRecallRound byMemberAndAndRound = memoryRecallRoundRepository.findByMemberAndAndRound(
        member, roundId);
    if (byMemberAndAndRound != null) {
      return byMemberAndAndRound;
    } else {
      throw new IllegalArgumentException("Invalid userId or roundId");
    }
  }
}
