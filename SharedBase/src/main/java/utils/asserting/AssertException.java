package utils.asserting;

public class AssertException extends RuntimeException {
  public AssertException(String message) {
    super(message);
  }

  public AssertException(String message, Throwable cause) {
  }
}
