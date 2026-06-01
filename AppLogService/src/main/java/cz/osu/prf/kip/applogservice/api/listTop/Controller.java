package cz.osu.prf.kip.applogservice.api.listTop;

import cz.osu.prf.kip.applogservice.db.AppLogItem;
import cz.osu.prf.kip.applogservice.db.AppLogItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/app-log")
public class Controller {

  @Autowired
  private AppLogItemRepository repository;

  private static List<Response> to(List<AppLogItem> tmp) {
    var ret = tmp.stream().map(Controller::to).toList();
    return ret;
  }

  private static Response to(AppLogItem tmp) {
    Response ret = new Response(
            tmp.getId(), tmp.getTimestamp(), tmp.getSourceServiceName(), tmp.getLogLevel(), tmp.getMessage());
    return ret;
  }

  @GetMapping()
  public List<Response> getTop(@RequestParam int count) {
    var tmp = repository.findAll(AppLogItemRepository.Pagings.getOrderByTimestampDesc(0, count)).getContent();
    var ret = to(tmp);
    return ret;
  }
}
