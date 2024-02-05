package com.api.farmingsoon.common.alert.discord;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
@RequiredArgsConstructor
public class DiscordService {

    @Value("${webhook.discord.url}")
    private String discordUrl;

    public void sendDiscordAlertLog(String errorCode, String errorMessage, HttpServletRequest request) {
        try {
            DiscordUtil discordUtil = new DiscordUtil(discordUrl);
            String registeredTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now());

            discordUtil.setUsername("farming");

            DiscordUtil.EmbedObject embedObject = new DiscordUtil.EmbedObject()
                    .setTitle("\uD83D\uDEA8 서버에 에러가 감지되었습니다. 즉시 확인이 필요합니다. \uD83D\uDEA8")
                    .setColor(Color.RED)

                    .addField("Request IP", request.getRemoteAddr(), true)
                    .addField("Request URL", request.getRequestURL() + "   " + request.getMethod(), true)
                    .addField("Error Code", errorCode, false)
                    .addField("Error Message", errorMessage, true)
                    .addField("발생 시간", registeredTimeFormat, false);

            discordUtil.addEmbed(embedObject);
            discordUtil.execute();
        } catch (Exception e) {
            log.debug("Discord 통신 과정에 예외 발생");
        }
    }

}