package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.data.message.*;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
                    if ("USER".equals(entity.getRole())) {
                        return UserMessage.from(entity.getMessage());
                    } else {
                        return AiMessage.from(entity.getMessage());
                    }
                })
                .collect(Collectors.toList());
    }
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        // 기존 메시지 삭제
        repository.deleteByMemoryId((Integer) memoryId);

        List<UserChatMemory> entities = messages.stream()
                .map(message -> {
                    String role;
                    String content;

                    if (message instanceof UserMessage) {
                        role = "USER";
                        content = ((UserMessage) message).contents().stream().filter(c -> c instanceof TextContent)
                                .map(c -> ((TextContent) c).text())
                                .collect(Collectors.joining("-"));

                    } else if (message instanceof AiMessage) {
                        role = "ASSISTANT";
                        content = ((AiMessage) message).text();  // String 반환
                    } else {
                        throw new IllegalArgumentException("지원하지 않는 Message 타입입니다.");
                    }

                    return UserChatMemory.builder()
                            .memoryId((Integer) memoryId)
                            .role(role)
                            .message(content)
                            .createdAt(LocalDateTime.now())
                            .build();
                })
                .collect(Collectors.toList());

        repository.saveAll(entities);
    }




    @Override
    public void deleteMessages(Object memoryId) {
        repository.deleteByMemoryId((Integer) memoryId);
    }
}
