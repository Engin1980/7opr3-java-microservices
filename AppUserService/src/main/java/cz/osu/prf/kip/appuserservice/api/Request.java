package cz.osu.prf.kip.appuserservice.api;

import utils.asserting.Assert;
import utils.asserting.AssertValidable;

public record Request(String email, String name, String surname) implements AssertValidable {
  @Override
  public void assertValid() {
    Assert.Args.isNotNull(name, "name");
    Assert.Args.isNotNull(surname, "surname");
    Assert.Args.isRegexMatch(email, "^[A-Za-z0-9+_.-]+@(.+)$", "email");
  }
}
