package deepscent_cnu.deepscent_cnu_api.config.gpt.dto;

import java.util.List;

public record RequestToAi(
    String model,
    List<Message> messages
) {

}
