package cz.osu.prf.kip.applogservice.api.create;

import cz.osu.prf.kip.applogservice.db.AppLogItem;
import cz.osu.prf.kip.applogservice.db.AppLogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import utils.asserting.Assert;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/app-log")
public class Controller {

  @Autowired
  private AppLogItemRepository repository;

  private static AppLogItem to(@RequestBody Request request) {
    Assert.IsValid(request);

    AppLogItem ret = new AppLogItem();
    ret.setMessage(request.message());
    ret.setLogLevel(request.getLogLevel());
    ret.setSourceServiceName(request.serviceName());
    ret.setTimestamp(LocalDateTime.now());

    return ret;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public void create(@RequestBody Request request) {
    AppLogItem appLogItem = to(request);
    repository.save(appLogItem);
  }
}
