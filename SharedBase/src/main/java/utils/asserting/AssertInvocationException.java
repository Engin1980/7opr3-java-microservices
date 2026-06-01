package utils.asserting;

public class AssertInvocationException extends AssertException {
  public AssertInvocationException(String message, Exception cause) {
    super("Assert calculation failed for test: " + message, cause);
  }
}
