package cz.osu.prf.kip.applogservice.api.create;

import cz.osu.prf.kip.applogservice.db.LogLevel;
import utils.asserting.AssertValidable;
import utils.asserting.Assert;

public record Request(String serviceName, String logLevel, String message) implements AssertValidable {

  public LogLevel getLogLevel() {
    return null;
  }

  public void assertValid() {
    Assert.isNotEmpty(serviceName, "Service name must not be null");
    Assert.isNotEmpty(logLevel, "Log level must not be null");
    Assert.isNotEmpty(message, "Message must not be null");
    Assert.isInvocable(this::getLogLevel, "Unable to evaluate log level");
  }
}
