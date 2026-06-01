package cz.osu.prf.kip.applogservice.api.listPage;

import cz.osu.prf.kip.applogservice.db.AppLogItem;
import cz.osu.prf.kip.applogservice.db.AppLogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app-log")
public class Controller {
  @Autowired
  private AppLogItemRepository repository;

  private static Response to(AppLogItem appLogItem) {
    return new Response(appLogItem.getId(), appLogItem.getTimestamp(), appLogItem.getSourceServiceName(), appLogItem.getLogLevel(), appLogItem.getMessage());
  }

  @GetMapping(path = "/page={page}&size={size}")
  public List<Response> getPage(int page, int size) {
    var tmp = repository.findAll(AppLogItemRepository.Pagings.getOrderByTimestampDesc(page, size)).getContent();
    var ret = tmp.stream().map(Controller::to).toList();
    return ret;
  }
}
