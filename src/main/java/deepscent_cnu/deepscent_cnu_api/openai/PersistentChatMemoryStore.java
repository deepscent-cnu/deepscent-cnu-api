package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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


  private static final int QUESTION_LIMIT = 10;
  private static final Pattern Q_PATTERN = Pattern.compile("^\\s*\\[Q(\\d{1,2})]\\s*");
  private static final Pattern SUMMARY_PATTERN =
      Pattern.compile("^\\s*\\[(?:요약|SUMMARY)]\\s*", Pattern.CASE_INSENSITIVE);

  private static boolean isAssistant(UserChatMemory m) {
    return "ASSISTANT".equals(m.getRole());
  }

  private static boolean isQuestion(String text) {
    return text != null && text.stripLeading().startsWith("[Q");
  }

  private static boolean isSummary(String text) {
    return text != null && text.stripLeading().startsWith("[SUMMARY]");
  }

  private static int extractQNumber(String text) {
    if (text == null) return -1;
    Matcher m = Q_PATTERN.matcher(text.stripLeading());
    return m.find() ? Integer.parseInt(m.group(1)) : -1;
  }

  // 한 메시지에 [Q]가 여러 개 들어온 경우 첫 번째 줄만 남김(질문 1개/턴 강제)
  private static String keepOnlyFirstQLineIfAny(String text) {
    if (text == null) return null;
    String[] lines = text.split("\\R");
    for (String line : lines) {
      if (Q_PATTERN.matcher(line.stripLeading()).find()) return line.strip();
    }
    return text; // [Q] 없으면 원문 유지
  }

  private static String normalizeQNumber(String text, int expected) {
    if (text == null) return null;
    return text.replaceFirst("\\[Q\\d+\\]", "[Q" + expected + "]");
  }

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


    // DB에 저장된 향기 사용
    String scent = Optional.ofNullable(memoryRecallRound.getScent())
        .filter(s -> !s.isBlank())
        .orElse("라벤더"); // fallback


    // 1. 기존 메시지 조회
    List<UserChatMemory> existingMessages = repository.findByMemoryRecallRoundOrderByCreatedAtAsc(
        memoryRecallRound);


    if (existingMessages.isEmpty()) {
      Long round = memoryRecallRound.getRound();
      String SESSION = sessionTitleOf(round);
      String template = """
                  당신은 65세 이상 경도인지장애(MCI) 노인을 위한 향기 기반 기억 회상 도우미 AI입니다.
                  이번 회기 주제: ${SESSION}
                  지금 사용자가 맡은 향기는 '${SCENT}'입니다.
                  당신의 역할:
                  - 향기, 회기 주제, 일상 맥락을 단서로 사용자가 자서전적 기억을 떠올리도록 돕습니다.
                  - 질문은 짧고 한 번에 하나씩, 사용자의 말에 공감 피드백을 준 뒤 다음 질문을 이어갑니다.
                  - 후속 질문은 **최대 10회**까지만 허용하고, 이후 요약 및 종료 멘트로 마무리합니다.
                  
                  출력 규칙(중요):
                  - 질문은 **"[Q1] ", "[Q2] ", …, "[Q10] "**으로 시작하며, 한 메시지에 질문은 하나만 포함합니다.
                  - "[Q10]" 다음에는 질문을 더 하지 말고, **"[요약]"**로 시작하는 2~3문장 요약과 종료 멘트를 출력합니다.
                  
                  유의사항:
                  - 질문은 너무 길거나 반복적이지 않도록 합니다.
                  - 감정적 피드백은 질문 사이에 짧게 제시해 사용자의 경험을 인정합니다.
                  """;
      String systemPrompt = template
          .replace("${SCENT}", scent)
          .replace("${SESSION}", SESSION);

      UserChatMemory systemMessage = UserChatMemory.builder()
//          .memoryId(id)
          .memoryRecallRound(memoryRecallRound)
          .role("SYSTEM")
          .message(systemPrompt)
          .createdAt(LocalDateTime.now())
          .build();

      repository.save(systemMessage); //  실제 DB에 저장됨
      existingMessages.add(systemMessage); //  이후 로직에서도 사용 가능
    }
    long qCountSoFar = existingMessages.stream()
        .filter(PersistentChatMemoryStore::isAssistant)
        .map(UserChatMemory::getMessage)
        .filter(PersistentChatMemoryStore::isQuestion)
        .count();

    boolean hasSummaryAlready = existingMessages.stream()
        .filter(PersistentChatMemoryStore::isAssistant)
        .map(UserChatMemory::getMessage)
        .anyMatch(PersistentChatMemoryStore::isSummary);

    // 3) 기존 중복 키
    Set<String> existingMessageKeys = existingMessages.stream()
        .map(m -> m.getRole() + "::" + m.getMessage())
        .collect(Collectors.toSet());

    // 4) 새 메시지 매핑 + 정책 강제  ← 변경
    List<UserChatMemory> messagesToSave = new ArrayList<>();
    for (ChatMessage message : newMessages) {
      String role;
      String content;

      if (message instanceof UserMessage um) {
        role = "USER";
        content = um.contents().stream()
            .filter(c -> c instanceof TextContent)
            .map(c -> ((TextContent) c).text())
            .collect(Collectors.joining("-"));

      } else if (message instanceof AiMessage am) {
        role = "ASSISTANT";
        content = am.text();

        // 한 메시지에 [Q…] 여러 줄이면 첫 줄만 저장
        if (content != null && content.contains("[Q")) {
          content = keepOnlyFirstQLineIfAny(content);
        }

        // (A) 조기 요약 차단: 10문 이전 [요약]/[SUMMARY]는 저장하지 않음
        if (isSummary(content) && qCountSoFar < QUESTION_LIMIT) {
          continue;
        }

        // (B) 초과 질문 차단: 10문 이후 [Q…]는 저장하지 않음
        if (isQuestion(content) && qCountSoFar >= QUESTION_LIMIT) {
          continue;
        }

        // (C) 질문이면 번호 보정 + 카운트 업
        if (isQuestion(content)) {
          int expected = (int) (qCountSoFar + 1);
          int got = extractQNumber(content);
          if (got != expected) content = normalizeQNumber(content, expected);
          qCountSoFar++;
        }

      } else if (message instanceof SystemMessage sm) {
        role = "SYSTEM";
        content = sm.text();

      } else {
        throw new IllegalArgumentException("지원하지 않는 Message 타입입니다.");
      }

      UserChatMemory m = UserChatMemory.builder()
          .role(role)
          .memoryRecallRound(memoryRecallRound)
          .message(content)
          .createdAt(LocalDateTime.now())
          .build();

      String key = m.getRole() + "::" + m.getMessage();
      boolean dup = existingMessageKeys.contains(key)
          || messagesToSave.stream().anyMatch(e -> (e.getRole() + "::" + e.getMessage()).equals(key));
      if (!dup) messagesToSave.add(m);
    }

    // 4. 새 메시지만 저장
    if (!messagesToSave.isEmpty()) {
      repository.saveAll(messagesToSave);
    }
  }

  @Override
  public void deleteMessages(Object memoryId) {

    repository.deleteByMemoryId((MemoryRecallRound) memoryId);
  }

  private static String sessionTitleOf(Long round) {
    int n = (round == null) ? 0 : round.intValue();
    return switch (n) {
      case 1 -> "어린 시절";
      case 2 -> "가족";
      case 3 -> "학교/학창 시절";
      case 4 -> "결혼/연애";
      case 5 -> "자녀/육아";
      case 6 -> "취미와 여가";
      case 7 -> "일과 사회생활";
      case 8 -> "나의 삶, 지금의 나";
      default -> "회기 미정";
    };
  }
}
