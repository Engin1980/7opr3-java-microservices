package cz.osu.prf.kip.appuserservice.api.update;

import cz.osu.prf.kip.appuserservice.api.Request;
import cz.osu.prf.kip.appuserservice.db.AppUser;
import cz.osu.prf.kip.appuserservice.db.AppUserRepository;
import exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/app-user")
@RestController
public class Controller {
  @Autowired
  private AppUserRepository appUserRepository;

  @PatchMapping("/{id}")
  public int create(@PathVariable() int id, @RequestBody Request request) {
    request.assertValid();

    AppUser appUser = appUserRepository.findById((long) id)
            .orElseThrow(() -> new EntityNotFoundException(AppUser.class, id));

    update(appUser, request);

    appUserRepository.save(appUser);
    return appUser.getId();
  }

  private void update(AppUser appUser, Request request) {
    appUser.setEmail(request.email().toLowerCase());
    appUser.setName(request.email());
    appUser.setSurname(request.surname());
  }

}
