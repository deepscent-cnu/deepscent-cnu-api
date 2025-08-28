package deepscent_cnu.deepscent_cnu_api.openai;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;

public interface Assistant {

  String chat(@MemoryId Long roundId, @UserMessage String userMessage);
}
