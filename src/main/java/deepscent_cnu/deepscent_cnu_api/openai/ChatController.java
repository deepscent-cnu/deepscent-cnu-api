package deepscent_cnu.deepscent_cnu_api.openai;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/api/chat/{userId}")
    public String chat(@PathVariable Integer userId,@RequestBody ChatRequest request) {
        return chatService.chat(userId, request.getUserMessage());
    }

}
