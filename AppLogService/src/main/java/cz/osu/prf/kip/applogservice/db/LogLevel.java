package cz.osu.prf.kip.applogservice.db;

public enum LogLevel {
  DEBUG,
  INFO,
  ERROR,
  WARNING;

  public static LogLevel parse(String value) {
    value = value.trim().toUpperCase();
    LogLevel ret = switch (value) {
      case "DBG", "DEBUG" -> DEBUG;
      case "INFORMATION", "INFO" -> INFO;
      case "ERROR", "ERR" -> ERROR;
      case "WARNING", "WARN" -> WARNING;
      default -> throw new IllegalArgumentException("Unknown log level string: " + value);
    };
    return ret;
  }
}
