package com.github.zlvkas.laby.twitch.util;

import com.github.zlvkas.laby.twitch.module.TwitchModule;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class TwitchUtil {

  private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.141 Safari/537.36 OPR/73.0.3856.438";
  private static final ScheduledExecutorService EXECUTOR_SERVICE = Executors
      .newScheduledThreadPool(1);
  private static final JsonParser JSON_PARSER = new JsonParser();

  private TwitchModule twitchModule;
  private String currentName = "";
  private String apiToken;
  private String channelId;
  private String followers = "Loading...";

  public TwitchUtil() {
    this.apiToken = this.getWebsiteContent(
        "https://raw.githubusercontent.com/zLvkas/LabyTwitch/master/assets/api-token.txt", false);
    if (this.apiToken == null) {
      this.followers = "Unable to request api token. Please restart your game.";
    }
  }

  public void startUpdater(JsonObject config) {
    if (this.apiToken == null) {
      return;
    }

    EXECUTOR_SERVICE.scheduleAtFixedRate(() -> {
      if (!this.currentName.equals(config.get("twitchName").getAsString())) {
        this.updateName(config);
        this.currentName = config.get("twitchName").getAsString();
      }

      if (this.twitchModule != null && this.channelId != null && this.twitchModule.isShown()) {
        final String raw = this
            .getWebsiteContent("https://api.twitch.tv/v5/channels/" + this.channelId, true);
        if (raw == null) {
          this.followers = "Unable to request followers. Please restart your game or wait a view seconds.";
          return;
        }

        final JsonObject jsonObject = JSON_PARSER.parse(raw).getAsJsonObject();
        this.followers = jsonObject.get("followers").getAsString();
      }
    }, 5L, 5L, TimeUnit.SECONDS);
  }

  private void updateName(JsonObject config) {
    final String name = config.get("twitchName").getAsString();
    if (name.isEmpty()) {
      this.followers = "Please set the channel name in the addon settings.";
      this.channelId = null;
      return;
    }

    final String rawData = this.getWebsiteContent(String
        .format("https://counts.live/api/twitch-follower-count/%s/data", name), false);
    if (rawData == null) {
      this.followers = "Channel " + name + " not found.";
      this.channelId = null;
      return;
    }

    final JsonObject dataObject = JSON_PARSER.parse(rawData).getAsJsonObject().get("data")
        .getAsJsonObject();
    this.channelId = dataObject.get("id").getAsString();
  }

  public String getWebsiteContent(String url, boolean twitchRequest) {
    try {
      final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
      connection.setRequestMethod("GET");
      connection.setRequestProperty("User-Agent", USER_AGENT);
      if (twitchRequest) {
        connection.setRequestProperty("client-id", this.apiToken);
      }

      connection.setReadTimeout(2500);
      connection.setConnectTimeout(2500);
      connection.connect();

      if (connection.getResponseCode() != 200) {
        return null;
      }

      final ByteArrayOutputStream result = new ByteArrayOutputStream();
      try (InputStream inputStream = connection.getInputStream()) {
        byte[] buffer = new byte[64];
        int length;
        while ((length = inputStream.read(buffer)) != -1) {
          result.write(buffer, 0, length);
        }
      }

      connection.disconnect();
      return result.toString(StandardCharsets.UTF_8.name());
    } catch (IOException exception) {
      this.followers = "An error occurred. Please restart your game.";
      exception.printStackTrace();
    }

    return null;
  }

  public void setTwitchModule(TwitchModule twitchModule) {
    this.twitchModule = twitchModule;
  }

  public String getFollowers() {
    return this.followers;
  }
}