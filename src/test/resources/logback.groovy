import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender

import java.nio.charset.Charset

import static ch.qos.logback.classic.Level.WARN

appender('STDOUT', ConsoleAppender) {
  encoder(PatternLayoutEncoder) {
    charset = Charset.forName("utf8")
    pattern = '%d{yyyy-MM-dd HH:mm:ss.SSS} ' +  // Date
              '%5p ' +                          // Log level
              '--- [%15.15t] ' +                // Thread
              '%-40.40logger{39} : ' +          // Logger
              '%m%n'                            // Message
  }
}
root(WARN, ['STDOUT'])
