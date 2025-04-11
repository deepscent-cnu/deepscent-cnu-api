package deepscent_cnu.deepscent_cnu_api.exception;

public class MemberException extends ApplicationException {
    public MemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
