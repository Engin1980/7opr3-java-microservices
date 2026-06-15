package cz.osu.prf.kip.appuserservice.api.get;

import cz.osu.prf.kip.appuserservice.db.AppUser;
import cz.osu.prf.kip.appuserservice.db.AppUserRepository;
import exceptions.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/app-user/")
@RestController
public class Controller {

  @Autowired
  private AppUserRepository appUserRepository;

  @GetMapping("/{id}")
  public Response getAppUser(@PathVariable int id) {
    AppUser tmp = appUserRepository.findById((long) id).orElseThrow(
            () -> new EntityNotFoundException(AppUser.class, id));
    Response ret = to(tmp);
    return ret;
  }

  private Response to(AppUser appUser) {
    Response ret = new Response(
            appUser.getId(),
            appUser.getEmail(),
            appUser.getName(),
            appUser.getSurname()
    );
    return ret;
  }
}
