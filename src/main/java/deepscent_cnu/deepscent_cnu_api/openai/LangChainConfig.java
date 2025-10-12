package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class LangChainConfig {

  private final PersistentChatMemoryStore chatMemoryStore;

  @Value("${openai.api-key}")
  private String OPEN_API_KEY;

  @Bean
  public ChatLanguageModel chatModel() {
    return OpenAiChatModel.builder()
        .apiKey(OPEN_API_KEY)
        .modelName("gpt-4o") // 16k 토큰 모델
        .logRequests(true)
        .logResponses(true)
        .build();
  }

  @Bean
  public Assistant assistant(ChatLanguageModel chatLanguageModel) {
    return AiServices.builder(Assistant.class)
        .chatLanguageModel(chatLanguageModel)
        .chatMemoryProvider(id ->
            MessageWindowChatMemory.builder()
                .id(id)
                .maxMessages(50)
                .chatMemoryStore(chatMemoryStore)
                .build())
        .build();
  }
}
