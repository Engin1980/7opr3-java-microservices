package cz.osu.prf.kip.applogservice.db;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AppLogItemRepository extends JpaRepository<AppLogItem, Integer> {
  class Pagings {
    public static Pageable getOrderByTimestampDesc(int page, int size) {
      return PageRequest.of(page, size, Sort.by("timestamp").descending());
    }
  }
}
