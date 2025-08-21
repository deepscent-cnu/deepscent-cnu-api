package deepscent_cnu.deepscent_cnu_api.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
public class ChatController {

  private final ChatService chatService;

  @PostMapping("/api/chat/{userId}")
  public String chat(@PathVariable("userId") Integer userId, @RequestBody ChatRequest request) {
    return chatService.chat(userId, request.getUserMessage());
  }

  //TEST용
  @PostMapping("/api/chat1/{userId}")
  public String chat1(@PathVariable("userId") Integer userId, @RequestBody ChatRequest request) {
    return chatService.chat(userId, request.getUserMessage());
  }
}
