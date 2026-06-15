package cz.osu.prf.kip.appuserservice.api.create;

import cz.osu.prf.kip.appuserservice.api.Request;
import cz.osu.prf.kip.appuserservice.db.AppUser;
import cz.osu.prf.kip.appuserservice.db.AppUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/app-user")
public class Controller {

  @Autowired
  private AppUserRepository appUserRepository;

  @PostMapping
  public int create(@RequestBody Request request) {
    request.assertValid();
    AppUser appUser = to(request);
    appUserRepository.save(appUser);
    return appUser.getId();
  }

  private AppUser to(Request request) {
    AppUser ret = new AppUser();
    ret.setEmail(request.email().toLowerCase());
    ret.setName(request.name());
    ret.setSurname(request.surname());
    return ret;
  }

}
