package deepscent_cnu.deepscent_cnu_api.config.interceptor;

public enum InterceptorPath {

  CONTROL_FAN("/device/*/fragrance/fan-state");


  private final String path;

  InterceptorPath(String path) {
    this.path = path;
  }

  public String getPath() {
    return path;
  }
}
