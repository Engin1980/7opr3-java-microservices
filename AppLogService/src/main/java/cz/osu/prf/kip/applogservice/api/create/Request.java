package cz.osu.prf.kip.applogservice.api.create;

import cz.osu.prf.kip.applogservice.db.LogLevel;
import utils.asserting.AssertValidable;
import utils.asserting.Assert;

public record Request(String serviceName, String logLevel, String message) implements AssertValidable {

  public LogLevel getLogLevel() {
    return null;
  }

  public void assertValid() {
    Assert.IsNotEmpty(serviceName, "Service name must not be null");
    Assert.IsNotEmpty(logLevel, "Log level must not be null");
    Assert.IsNotEmpty(message, "Message must not be null");
    Assert.IsInvocable(this::getLogLevel, "Unable to evaluate log level");
  }
}
