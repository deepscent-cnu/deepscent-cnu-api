package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class PersistentChatMemoryStore implements ChatMemoryStore {

  private final UserChatMemoryRepository repository;
  private final MemoryRecallRoundRepository memoryRecallRoundRepository;

  @Override
  public List<ChatMessage> getMessages(Object roundId) {
    roundId = (Long) roundId;
    MemoryRecallRound memberRecallRound = memoryRecallRoundRepository.findById((Long) roundId)
        .orElseThrow(() -> new IllegalArgumentException("Invalid memoryId: "));
    return repository.findByMemoryRecallRoundOrderByCreatedAtAsc(memberRecallRound)
        .stream()
        .map(entity -> {
          switch (entity.getRole()) {
            case "USER":
              return UserMessage.from(entity.getMessage());
            case "ASSISTANT":
              return AiMessage.from(entity.getMessage());
            case "SYSTEM":
              return SystemMessage.from(entity.getMessage());
            default:
              throw new IllegalArgumentException("지원하지 않는 역할: " + entity.getRole());
          }
        })

        .collect(Collectors.toList());
  }

  @Override
  public void updateMessages(Object roundId, List<ChatMessage> newMessages) {
    Long id = (Long) roundId;
    MemoryRecallRound memoryRecallRound = memoryRecallRoundRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Invalid memoryId: " + id));
    String scent = "참기름향기"; // 향기 이름을 여기에 지정하세요. 예: "장미", "바닐라" 등

    // 1. 기존 메시지 조회
    List<UserChatMemory> existingMessages = repository.findByMemoryRecallRoundOrderByCreatedAtAsc(
        memoryRecallRound);
    if (existingMessages.isEmpty()) {
      UserChatMemory systemMessage = UserChatMemory.builder()
//          .memoryId(id)
          .memoryRecallRound(memoryRecallRound)
          .role("SYSTEM")
          .message(
              """
                  당신은 65세 이상 경도인지장애(MCI) 노인을 위한 향기 기반 기억 회상 도우미 AI입니다.
                  지금 사용자가 맡은 향기는 '%s'입니다.
                  당신의 역할:
                  - 향기 자극을 바탕으로 사용자의 감각 경험, 감정 반응, 자서전적 기억을 자연스럽게 이끌어냅니다.
                  - 매 단계별 응답에 감정적으로 공감하며, 친근하고 따뜻한 어조를 유지합니다.
                  - 질문은 매번 새롭게 생성하되, 사용자 응답을 바탕으로 자연스럽게 이어지도록 구성합니다.
                  - 후속 질문은 **최대 9회**까지만 허용하고, 이후 **요약 및 종료 멘트**로 마무리합니다.
                                      
                  대화 흐름:
                  1. 향기 인식 → 감정 반응 → 기억 회상 → 후속 질문 → 요약 및 종료
                  예시 질문 흐름 (단 고정된 문장은 아님):
                  - “방금 맡은 냄새는 어떤 느낌이 드셨나요?”
                  - “그 향기를 맡았을 때 기분은 어떠셨어요?”
                  - “이 향기가 떠오르게 한 기억이 있으신가요?”
                  - [기억 있음 시] “그때는 어디였고 누구와 함께 있었나요?”
                  - [기억 없음 시] “혹시 시장이나 시골집, 학교 같은 공간이 생각나시나요?”
                  요약 및 종료 멘트 예시:
                  - “오늘은 [누구와], [어디서], [언제쯤] 있었던 추억을 떠올리셨습니다.”
                  - “오늘의 회상은 여기까지입니다. 다음에 또 뵙겠습니다. 🌿”
                  유의사항:
                  - 질문은 너무 길거나 반복적이지 않도록 합니다.
                  - 감정적 피드백은 질문 사이에 짧게 제시해 사용자의 경험을 인정합니다.
                  - 무응답이나 막힘이 있는 경우에는 간단한 힌트를 제공해 회상을 유도합니다"""
                  .formatted(scent))
          .createdAt(LocalDateTime.now())
          .build();

      repository.save(systemMessage); //  실제 DB에 저장됨
      existingMessages.add(systemMessage); //  이후 로직에서도 사용 가능
    }

    // 2. 기존 메시지를 문자열 기반으로 매핑
    Set<String> existingMessageKeys = existingMessages.stream()
        .map(m -> m.getRole() + "::" + m.getMessage())
        .collect(Collectors.toSet());

    // 3. 새 메시지 중 DB에 없는 것만 추려냄
    List<UserChatMemory> messagesToSave = newMessages.stream()
        .map(message -> {
          String role;
          String content;

          if (message instanceof UserMessage) {
            role = "USER";
            content = ((UserMessage) message).contents().stream()
                .filter(c -> c instanceof TextContent)
                .map(c -> ((TextContent) c).text())
                .collect(Collectors.joining("-"));

          } else if (message instanceof AiMessage) {
            role = "ASSISTANT";
            content = ((AiMessage) message).text();
          } else if (message instanceof SystemMessage) {
            role = "SYSTEM";
            content = ((SystemMessage) message).text();
          } else {
            throw new IllegalArgumentException("지원하지 않는 Message 타입입니다.");
          }

          return UserChatMemory.builder()
//              .memoryId(id)
              .role(role)
              .memoryRecallRound(memoryRecallRound)
              .message(content)
              .createdAt(LocalDateTime.now()) // 실제 상황에서는 시간 보정 필요
              .build();
        })
        .filter(m -> !existingMessageKeys.contains(m.getRole() + "::" + m.getMessage())) // 중복 제거
        .collect(Collectors.toList());

    // 4. 새 메시지만 저장
    if (!messagesToSave.isEmpty()) {
      repository.saveAll(messagesToSave);
    }
  }

  @Override
  public void deleteMessages(Object memoryId) {

    repository.deleteByMemoryId((MemoryRecallRound) memoryId);
  }
}
