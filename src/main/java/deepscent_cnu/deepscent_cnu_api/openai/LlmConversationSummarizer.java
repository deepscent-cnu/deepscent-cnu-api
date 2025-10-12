package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LlmConversationSummarizer implements ConversationSummarizer {

  private static final String SUMMARY_PROMPT = """
      다음은 한 회차 동안 '사용자'가 말한 내용만 시간순으로 모은 원문입니다.
      향기 단서: ${SCENT}

      요약 지침(중요):
      - 출력은 **한국어 한글**로만 작성합니다.
      - **딱 아래 형식**으로만 출력합니다. 다른 말머리/대괄호/해시태그/화자표시는 절대 쓰지 마세요.
      - 사건의 핵심(누구/어디/언제쯤/무엇/느낌)이 드러나도록 1~2문장으로 요약하고, 마지막 줄에 키워드 3개를 쉼표로 제시하세요.
      - 키워드는 꼭 한줄만 출력합니다.

      원문(사용자 발화만 제공됨):
      ${TRANSCRIPT}
      """;
  private final ChatLanguageModel model;

  @Override
  public String summarizeFromRawText(String userTranscript, String scent) {
    String filled = SUMMARY_PROMPT
        .replace("${SCENT}", (scent == null || scent.isBlank()) ? "미상" : scent)
        .replace("${TRANSCRIPT}", userTranscript == null ? "" : userTranscript);

    String raw = model.chat(filled);

    // 안전 청소: 혹시 남은 대괄호/말머리 제거
    String cleaned = raw
        .replaceAll("\\[[^\\]]*\\]", "")   // [사용자], [무엇] 같은 태그 제거
        .replaceAll("(?m)^\\s*[-•▶]+\\s*", "") // 불릿 말머리 제거
        .trim();

    // 최소 보정: 키워드 라인이 없으면 한 줄 추가
    if (!cleaned.matches("(?s).*?\\b키워드\\s*:\\s*.+")) {
      cleaned = cleaned + "\n키워드: 기억, 장소, 사람";
    }
    return cleaned;
  }
}
