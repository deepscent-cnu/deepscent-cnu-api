package deepscent_cnu.deepscent_cnu_api.exception;


import deepscent_cnu.deepscent_cnu_api.util.ApiResponse;
import java.util.NoSuchElementException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ApiResponse<Object>> handleCustomException(ApplicationException ex) {
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponse<>(false, ex.getMessage(), null));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Object>> handleValidationException(
      MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .findFirst()
        .orElse("입력값이 올바르지 않습니다.");
    return ResponseEntity
        .status(HttpStatus.BAD_REQUEST)
        .body(new ApiResponse<>(false, errorMessage, null));
  }

  @ExceptionHandler(value = IllegalArgumentException.class)
  public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(
      IllegalArgumentException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
    return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = NoSuchElementException.class)
  public ResponseEntity<ExceptionResponse> handleNoSuchElementException(
      NoSuchElementException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
    return new ResponseEntity<>(exceptionResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(value = MemberException.class)
  public ResponseEntity<ExceptionResponse> handleMemberException(
      MemberException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
    return new ResponseEntity<>(exceptionResponse, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(value = SlotMappingException.class)
  public ResponseEntity<ExceptionResponse> handleSlotMappingException(
      SlotMappingException e) {
    ExceptionResponse exceptionResponse = new ExceptionResponse(e.getMessage());
    return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception ex) {
    ex.printStackTrace();
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiResponse<>(false, "알 수 없는 오류가 발생했습니다.", null));
  }
}
