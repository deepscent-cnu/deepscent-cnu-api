package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
public class PersistentChatMemoryStore implements ChatMemoryStore {

    private final UserChatMemoryRepository repository;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return repository.findByMemoryIdOrderByCreatedAtAsc((Integer) memoryId)
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
    public void updateMessages(Object memoryId, List<ChatMessage> newMessages) {
        Integer id = (Integer) memoryId;
        String scent = "참기름향기"; // 향기 이름을 여기에 지정하세요. 예: "장미", "바닐라" 등

        // 1. 기존 메시지 조회
        List<UserChatMemory> existingMessages = repository.findByMemoryIdOrderByCreatedAtAsc(id);
        if (existingMessages.isEmpty()) {
            UserChatMemory systemMessage = UserChatMemory.builder()
                    .memoryId(id)
                    .role("SYSTEM")
                    .message(
                            "당신은 향기를 기반으로 고령자의 감각 기억과 감정 반응을 이끌어내는 회상 도우미 AI입니다." +
                                    " 사용자는 '%s' 향기를 맡았고, 당신은 다음 규칙에 따라 대화를 진행합니다. " +
                                    "- 질문은 매번 새롭게 생성하지만, 사용자의 응답 맥락을 반영해야 합니다. " +
                                    "- 향기에 대한 감각적 묘사 → 감정적 느낌 → 자서전적 기억 → 그 기억에 대한 맥락 → 감정적 마무리. " +
                                    "- 응답이 없거나 막힐 경우는 힌트를 제공합니다. " +
                                    "- 회상 후 요약 정리를 포함하세요."
                                            .formatted(scent))
                    .createdAt(LocalDateTime.now())
                    .build();

            repository.save(systemMessage); // ✅ 실제 DB에 저장됨
            existingMessages.add(systemMessage); // ✅ 이후 로직에서도 사용 가능
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
                            .memoryId(id)
                            .role(role)
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
        repository.deleteByMemoryId((Integer) memoryId);
    }
}
