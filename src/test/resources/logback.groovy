import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import static ch.qos.logback.classic.Level.WARN

appender('STDOUT', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    pattern = '%d{yyyy-MM-dd HH:mm:ss.SSS} %5p [%15.15t] %-40.40logger{39} : %msg%n'
  }
}
root(WARN, ['STDOUT'])
