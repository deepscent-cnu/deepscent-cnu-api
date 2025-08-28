package deepscent_cnu.deepscent_cnu_api.openai;

import deepscent_cnu.deepscent_cnu_api.auth.entity.Member;
import deepscent_cnu.deepscent_cnu_api.openai.dto.response.LastRoundResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

  private final Assistant assistant;

  private final ChatRepository chatRepository;

  public String chat(Long roundId, String userMessage) {

    return assistant.chat(roundId, userMessage);
  }

  public LastRoundResponse getRoundList(Member member) {
    List<MemoryRecallRound> memoryRecallRoundList = chatRepository.findAllByMember(member);
    int lastRound = 0;

    for (MemoryRecallRound memoryRecallRound : memoryRecallRoundList) {
      int round = memoryRecallRound.getRound().intValue();
      lastRound = Math.max(lastRound, round);
    }

    return new LastRoundResponse(lastRound);
  }
}
