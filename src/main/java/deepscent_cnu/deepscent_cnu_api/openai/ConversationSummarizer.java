package deepscent_cnu.deepscent_cnu_api.openai;

public interface ConversationSummarizer {
  String summarizeFromRawText(String userTranscript, String scent);

}
