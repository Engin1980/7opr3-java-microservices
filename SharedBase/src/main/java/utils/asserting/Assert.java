package utils.asserting;

import java.util.function.Supplier;

public class Assert {

  public static void isTrue(boolean value) {
    isTrue(value, "Expected value to be true, but was false.");
  }

  public static void isTrue(boolean value, String message) {
    isTrue(() -> value, () -> message);
  }

  public static void isTrue(Supplier<Boolean> value, Supplier<String> messageProvider) {
    isTrueOrExc(value, () -> new AssertException(messageProvider.get()));
  }

  public static void isTrueOrExc(Supplier<Boolean> condition, Supplier<RuntimeException> exceptionProvider) {
    try {
      if (!condition.get()) {
        RuntimeException exc = exceptionProvider.get();
        throw exc;
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }

  public static void isFalse(boolean value) {
    isFalse(value, "Expected value to be false, but was true.");
  }

  public static void isFalse(boolean value, String message) {
    isFalse(() -> value, () -> message);
  }

  public static void isFalse(Supplier<Boolean> value, Supplier<String> messageProvider) {
    isFalseOrExc(value, () -> new AssertException(messageProvider.get()));
  }

  public static void isFalseOrExc(Supplier<Boolean> value, Supplier<RuntimeException> exceptionProvider) {
    try {
      if (!value.get()) {
        RuntimeException exc = exceptionProvider.get();
        throw exc;
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }


  public static void isNull(Object value, String message) {
    isNull(() -> value, () -> message);
  }

  public static void isNull(Supplier<Object> value, Supplier<String> messageProvider) {
    isNullOrExc(value, () -> new AssertException(messageProvider.get()));
  }

  public static void isNullOrExc(Supplier<Object> value, Supplier<RuntimeException> exceptionProvider) {
    try {
      if (value.get() == null) {
        RuntimeException exc = exceptionProvider.get();
        throw exc;
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }

  public static void isNull(Object value) {
    isNull(value, "Expected value to be null.");
  }

  public static void isNotNull(Object value, String message) {
    isNotNull(() -> value, () -> message);
  }

  public static void isNotNull(Supplier<Object> value, Supplier<String> messageProvider) {
    isNotNullOrExc(() -> value, () -> new AssertException(messageProvider.get()));
  }

  public static void isNotNullOrExc(Supplier<Object> value, Supplier<RuntimeException> exceptionProvider) {
    try {
      if (value.get() == null) {
        RuntimeException exc = exceptionProvider.get();
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }

  public static void isNotNull(Object value) {
    isNotNull(value, "Expected value to be null.");
  }

  public static void isNotEmpty(String value, String message) {
    isNotEmpty(() -> value, () -> message);
  }

  public static void isNotEmpty(Supplier<String> value, Supplier<String> messageProvider) {
    isNotEmptyOrExc(value, () -> new AssertException(messageProvider.get()));
  }

  public static void isNotEmptyOrExc(Supplier<String> value, Supplier<RuntimeException> exceptionProvider) {
    try {
      String val = value.get();
      if (val == null || val.isEmpty()) {
        RuntimeException exc = exceptionProvider.get();
        throw exc;
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }

  public static void isNotEmpty(String value) {
    isNotEmpty(value, "Expected value is null or empty string.");
  }

  public static void isInvocable(Runnable code) {
    isInvocable(code, "Expected code to be invocable without exceptions, but an exception was thrown.");
  }

  public static void isInvocable(Runnable code, String message) {
    try {
      code.run();
    } catch (Exception e) {
      throw new AssertException(message);
    }
  }

  public static void isValid(AssertValidable value){
    Assert.isNotNull(value, "Expected value to be not null.");
    Assert.isInvocable(() -> value.assertValid(), "Expected value to be valid, but it is not.");
  }


  private static void isRegexMatchOrExc(Supplier<String> value, String regex, Supplier<RuntimeException> exceptionProvider) {
    try{
      String val = value.get();
      if (val == null || !val.matches(regex)) {
        RuntimeException exc = exceptionProvider.get();
        throw exc;
      }
    } catch(Exception e){
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }

  public static class Args {
    public static void isTrue(boolean value, String argName) {
      Assert.isTrueOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be true, but was false."));
    }

    public static void is(boolean value, String argName, String message) {
      Assert.isTrueOrExc(() -> value, () -> new IllegalArgumentException("Expression true-check failed for argument '" + argName + "'. " + message));
    }

    public static void isFalse(boolean value, String argName) {
      Assert.isFalseOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be false, but was true."));
    }

    public static void isNull(Object value, String argName) {
      Assert.isNullOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be null."));
    }

    public static void isNotNull(Object value, String argName) {
      Assert.isNotNullOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be not null."));
    }

    public static void isNotEmpty(String value, String argName) {
      Assert.isNotEmptyOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be not empty."));
    }

    public static void isRegexMatch(String value, String regex, String argName) {
      Assert.isRegexMatchOrExc(() -> value, regex, () -> new IllegalArgumentException("Argument '" + argName + "' is expecteed to match regex " + regex + ", but was '" + value + "'."));
    }
  }

}
