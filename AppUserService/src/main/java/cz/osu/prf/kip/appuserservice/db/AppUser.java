package cz.osu.prf.kip.appuserservice.db;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AppUser {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int id;

  @Setter
  private String email;
  @Setter
  private String name;
  @Setter
  private String surname;
}
