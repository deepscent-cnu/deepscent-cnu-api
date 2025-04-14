package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

@SystemMessage("""
                당신은 사람에게 향기에 대한 기억을 떠올리게 질문하는 AI입니다. +
                처음에는 사용자가 말한 향기와 관련된 질문을 하세요
                """)
public interface Assistant {
    String chat(@MemoryId Integer userId, @UserMessage String userMessage);
}
