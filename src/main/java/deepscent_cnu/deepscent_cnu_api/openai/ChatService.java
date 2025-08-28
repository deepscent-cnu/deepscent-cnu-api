package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.service.MemoryId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

  private final Assistant assistant;

  public String chat(Long roundId, String userMessage) {

    return assistant.chat(roundId, userMessage);
  }
}
