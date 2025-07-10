package deepscent_cnu.deepscent_cnu_api.whisper;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "openai")
@Getter
@Setter
public class WhisperProperties {

  private String apiKey;
  private String whisperUrl;
}
