package utils.asserting;

import java.util.function.Supplier;

public class Assert {

  public static void IsTrue(boolean value) {
    IsTrue(value, "Expected value to be true, but was false.");
  }

  public static void IsTrue(boolean value, String message) {
    IsTrue(() -> value, () -> message);
  }

  public static void IsTrue(Supplier<Boolean> value, Supplier<String> messageProvider) {
    IsTrueOrExc(value, () -> new AssertException(messageProvider.get()));
  }

  public static void IsTrueOrExc(Supplier<Boolean> condition, Supplier<RuntimeException> exceptionProvider) {
    try {
      if (!condition.get()) {
        RuntimeException exc = exceptionProvider.get();
        throw exc;
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }

  public static void IsFalse(boolean value) {
    IsFalse(value, "Expected value to be false, but was true.");
  }

  public static void IsFalse(boolean value, String message) {
    IsFalse(() -> value, () -> message);
  }

  public static void IsFalse(Supplier<Boolean> value, Supplier<String> messageProvider) {
    IsFalseOrExc(value, () -> new AssertException(messageProvider.get()));
  }

  public static void IsFalseOrExc(Supplier<Boolean> value, Supplier<RuntimeException> exceptionProvider) {
    try {
      if (!value.get()) {
        RuntimeException exc = exceptionProvider.get();
        throw exc;
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }


  public static void IsNull(Object value, String message) {
    IsNull(() -> value, () -> message);
  }

  public static void IsNull(Supplier<Object> value, Supplier<String> messageProvider) {
    IsNullOrExc(value, () -> new AssertException(messageProvider.get()));
  }

  public static void IsNullOrExc(Supplier<Object> value, Supplier<RuntimeException> exceptionProvider) {
    try {
      if (value.get() == null) {
        RuntimeException exc = exceptionProvider.get();
        throw exc;
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }

  public static void IsNull(Object value) {
    IsNull(value, "Expected value to be null.");
  }

  public static void IsNotNull(Object value, String message) {
    IsNotNull(() -> value, () -> message);
  }

  public static void IsNotNull(Supplier<Object> value, Supplier<String> messageProvider) {
    IsNotNullOrExc(() -> value, () -> new AssertException(messageProvider.get()));
  }

  public static void IsNotNullOrExc(Supplier<Object> value, Supplier<RuntimeException> exceptionProvider) {
    try {
      if (value.get() == null) {
        RuntimeException exc = exceptionProvider.get();
      }
    } catch (Exception e) {
      throw new AssertInvocationException(e.getMessage(), e);
    }
  }

  public static void IsNotNull(Object value) {
    IsNotNull(value, "Expected value to be null.");
  }

  public static void IsNotEmpty(String value, String message) {
    IsNotEmpty(() -> value, () -> message);
  }

  public static void IsNotEmpty(Supplier<String> value, Supplier<String> messageProvider) {
    IsNotEmptyOrExc(value, () -> new AssertException(messageProvider.get()));
  }

  public static void IsNotEmptyOrExc(Supplier<String> value, Supplier<RuntimeException> exceptionProvider) {
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

  public static void IsNotEmpty(String value) {
    IsNotEmpty(value, "Expected value is null or empty string.");
  }

  public static void IsInvocable(Runnable code) {
    IsInvocable(code, "Expected code to be invocable without exceptions, but an exception was thrown.");
  }

  public static void IsInvocable(Runnable code, String message) {
    try {
      code.run();
    } catch (Exception e) {
      throw new AssertException(message);
    }
  }

  public static void IsValid(AssertValidable value){
    Assert.IsNotNull(value, "Expected value to be not null.");
    Assert.IsInvocable(() -> value.assertValid(), "Expected value to be valid, but it is not.");
  }

  public static class Args {
    public static void IsTrue(boolean value, String argName) {
      Assert.IsTrueOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be true, but was false."));
    }

    public static void Is(boolean value, String argName, String message) {
      Assert.IsTrueOrExc(() -> value, () -> new IllegalArgumentException("Expression true-check failed for argument '" + argName + "'. " + message));
    }

    public static void IsFalse(boolean value, String argName) {
      Assert.IsFalseOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be false, but was true."));
    }

    public static void IsNull(Object value, String argName) {
      Assert.IsNullOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be null."));
    }

    public static void IsNotNull(Object value, String argName) {
      Assert.IsNotNullOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be not null."));
    }

    public static void IsNotEmpty(String value, String argName) {
      Assert.IsNotEmptyOrExc(() -> value, () -> new IllegalArgumentException("Argument '" + argName + "' is expected to be not empty."));
    }
  }
}
