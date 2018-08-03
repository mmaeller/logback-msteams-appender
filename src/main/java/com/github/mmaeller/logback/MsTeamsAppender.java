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
        addError("No webHookUri available!");
      } else {
        sendLog(event);
      }
    } catch (final Exception ex) {
      ex.printStackTrace();
      addError(String.format("Error posting log to %s: %s", webHookUri, event), ex);
    }
  }

  private void sendLog(final ILoggingEvent event) throws IOException {

    final MessageCard.Builder builder = MessageCard.builder();
    builder.title(event.getLoggerName());
    builder.text(event.getFormattedMessage());
    builder.themeColor(colorByLevel(event.getLevel()));

    postMessage(OBJECT_MAPPER.writeValueAsBytes(builder.build()));
  }

  private String colorByLevel(final Level level) {
    if (Level.ERROR.equals(level)) {
      return "ff1900";
    } else if (Level.WARN.equals(level)) {
      return "ffff00";
    } else if (Level.INFO.equals(level)) {
      return "009911";
    }
    return "ff00ff";
  }

  private void postMessage(final byte[] messageBytes) throws IOException {
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
