package cz.osu.prf.kip.applogservice.api.listPage;

import cz.osu.prf.kip.applogservice.db.LogLevel;
import java.time.LocalDateTime;

public record Response(long Id, LocalDateTime Timestamp, String ServiceName, LogLevel LogLevel, String Message) {
}