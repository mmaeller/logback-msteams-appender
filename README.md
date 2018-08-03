# Based on `logback-slack-appender` by [maricn](https://github.com/maricn/logback-slack-appender).

This is a simple [Logback](http://logback.qos.ch/) appender which pushes logs to a [MsTeams](https://products.office.com/en-us/microsoft-teams/group-chat-software) channel.

## How to setup

Add dependency to com.github.mmaeller:logback-msteams-appender:1.0.0 in your pom.xml.

Add MsTeamsAppender configuration to logback.xml file

```
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>

  <appender name="MSTEAMS" class="com.github.mmaeller.logback.MsTeamsAppender">
    <!-- MS Teams webhook URI -->
    <webHookUri>https://outlook.office.com/webhook/any-uuid/IncomingWebhook/any-other-uuid</webHookUri>
    <!-- Custom connection timeout (default 5 seconds) -->
    <connectionTimeout>1000</connectionTimeout>
    <!-- Custom read timeout (default 10 seconds) -->
    <readTimeout>3000</readTimeout>
  </appender>

  <!-- Currently recommended way of using MS Teams appender -->
  <appender name="ASYNC_MSTEAMS" class="ch.qos.logback.classic.AsyncAppender">
    <appender-ref ref="MSTEAMS" />
    <filter class="ch.qos.logback.classic.filter.ThresholdFilter">
      <level>ERROR</level>
    </filter>
  </appender>

  <root>
    <level value="ALL" />
    <appender-ref ref="ASYNC_MSTEAMS" />
  </root>

</configuration>
```
