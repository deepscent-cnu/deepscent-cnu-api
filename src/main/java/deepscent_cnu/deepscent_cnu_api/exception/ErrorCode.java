package deepscent_cnu.deepscent_cnu_api.exception;

public enum ErrorCode {
  USERNAME_ALREADY_EXISTS("이미 존재하는 아이디입니다."),
  USER_NOT_FOUND("존재하지 않는 사용자입니다."),
  INVALID_PASSWORD("비밀번호가 일치하지 않습니다."),
  INVALID_INPUT("입력값이 유효하지 않습니다.");

  private final String message;

  ErrorCode(String message) {
    this.message = message;
  }

  public String message() {
    return message;
  }
}
