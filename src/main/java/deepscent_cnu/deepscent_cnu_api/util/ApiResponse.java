package deepscent_cnu.deepscent_cnu_api.util;

public record ApiResponse<T>(boolean success, String message, T data) {

}
