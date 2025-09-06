package deepscent_cnu.deepscent_cnu_api.openai;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserChatMemoryRepository extends JpaRepository<UserChatMemory, Long> {

  List<UserChatMemory> findByMemoryRecallRoundOrderByCreatedAtAsc(
      MemoryRecallRound memoryRecallRound);

  void deleteByMemoryRecallRound(MemoryRecallRound memoryRecallRound);

  void deleteByMemoryId(MemoryRecallRound memoryRecallRound);

  @Query("""
         select m.message
         from UserChatMemory m
         where m.memoryRecallRound = :round
           and m.role = 'USER'
         order by m.createdAt asc
         """)
  List<String> findUserMessagesTextByRound(@Param("round") MemoryRecallRound round);
}
