package com.github.mmaeller.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MsTeamsAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  private String webHookUri;
  private int connectionTimeout = 5_000;
  private int readTimeout = 10_000;

  @Override
  protected void append(final ILoggingEvent event) {
    try {
      if (webHookUri == null || webHookUri.isEmpty()) {
        addError("No webHook URI available!");
      } else {
        postMessage(createMessage(event));
      }
    } catch (final Exception ex) {
      ex.printStackTrace();
      addError(String.format("Error posting log to %s: %s", webHookUri, event), ex);
    }
  }

  /**
   * Creates a {@link MessageCard} from the provided {@link ILoggingEvent}.
   *
   * @param event occurred {@link ILoggingEvent}
   * @return {@link MessageCard} with data from the event
   */
  private MessageCard createMessage(final ILoggingEvent event) {
    return MessageCard.builder()
        .title(event.getLoggerName())
        .text(event.getFormattedMessage())
        .themeColor(getThemeColorByLevel(event.getLevel()))
        .build();
  }

  /**
   * Determines predefined theme colors by the event's level.
   *
   * <ul>
   *   <li>ERROR: ff1900
   *   <li>WARN: ffff00
   *   <li>INFO: 009911
   *   <li>all other: ff00ff
   * </ul>
   *
   * @param level event's level
   * @return color code
   */
  private String getThemeColorByLevel(final Level level) {
    if (Level.ERROR.equals(level)) {
      return "ff1900";
    } else if (Level.WARN.equals(level)) {
      return "ffff00";
    } else if (Level.INFO.equals(level)) {
      return "009911";
    }
    return "ff00ff";
  }

  /**
   * Posts the {@link MessageCard} to the configured webhook URI.
   *
   * @param messageCard the data that shall be send
   * @throws IOException when the connection couldn't be established, the message couldn't be
   *     converted nor sent.
   */
  private void postMessage(final MessageCard messageCard) throws IOException {
    final byte[] messageBytes = OBJECT_MAPPER.writeValueAsBytes(messageCard);
    final HttpURLConnection conn = (HttpURLConnection) new URL(webHookUri).openConnection();
    conn.setConnectTimeout(connectionTimeout);
    conn.setReadTimeout(readTimeout);
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setFixedLengthStreamingMode(messageBytes.length);
    conn.setRequestProperty("Content-Type", "application/json");

    try (final OutputStream os = conn.getOutputStream()) {
      os.write(messageBytes);
      os.flush();
    } finally {
      conn.disconnect();
    }
  }

  public int getConnectionTimeout() {
    return connectionTimeout;
  }

  public void setConnectionTimeout(final int connectionTimeout) {
    this.connectionTimeout = connectionTimeout;
  }

  public int getReadTimeout() {
    return readTimeout;
  }

  public void setReadTimeout(final int readTimeout) {
    this.readTimeout = readTimeout;
  }

  public String getWebHookUri() {
    return webHookUri;
  }

  public void setWebHookUri(final String webHookUri) {
    this.webHookUri = webHookUri;
  }
}
