package deepscent_cnu.deepscent_cnu_api.exception;

import lombok.Getter;

@Getter
public class ApplicationException extends RuntimeException {

    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode) {
        super(errorCode.message());
        this.errorCode = errorCode;
    }

}
