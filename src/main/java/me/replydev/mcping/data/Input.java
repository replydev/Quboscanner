package me.replydev.mcping.data;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

public class Input {
    private static final Pattern STRIP_PATTERN = Pattern.compile("(?<!<@)[&§](?i)[0-9a-fklmnorx]");

    private Input() {
    }

    /**
     * Strips input of all Minecraft formatting goop
     *
     * @param input to strip
     * @return clean string
     */
    public static String stripMinecraft(String input) {
        if (input == null) return "";
        return StringUtils.trimToEmpty(STRIP_PATTERN.matcher(input).replaceAll(""));
    }
}
