package deepscent_cnu.deepscent_cnu_api.openai;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
public interface UserChatMemoryRepository extends JpaRepository<UserChatMemory, Long> {
    List<UserChatMemory> findByMemoryIdOrderByCreatedAtAsc(Integer memoryId);
    void deleteByMemoryId(Integer memoryId);
}
