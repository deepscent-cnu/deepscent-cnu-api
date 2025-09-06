package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LlmConversationSummarizer implements ConversationSummarizer{
  private final ChatLanguageModel model;


  @Override
  public String summarizeFromRawText(String userTranscript, String scent) {
    String prompt = """
      아래는 한 회차 동안 '사용자'가 말한 내용만 시간순으로 모은 텍스트입니다.
      향기 단서: %s
      - 5~7문장으로 사실 위주 요약
      - [누구와][어디서][언제][무엇][느낌]이 보이면 반영
      - 새 정보 추가 금지

      사용자 발화:
      %s
    """.formatted(scent == null ? "미상" : scent, userTranscript);
    return model.chat(prompt);
  }
}
