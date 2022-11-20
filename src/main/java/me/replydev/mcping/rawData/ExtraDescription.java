package me.replydev.mcping.rawData;

import com.google.gson.annotations.SerializedName;

public class ExtraDescription {

    @SuppressWarnings("MismatchedReadAndWriteOfArray")
    @SerializedName("extra")
    private Extra[] extra;


    public String getText() {
        StringBuilder s = new StringBuilder();
        for (Extra e : extra) {
            s.append(e.getText());
        }
        return s.toString();
    }
}
