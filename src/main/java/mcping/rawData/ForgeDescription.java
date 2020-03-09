package mcping.rawData;

import com.google.gson.annotations.SerializedName;

public class ForgeDescription {
    @SerializedName("translate")
    private String translate;

    public String getTranslate(){
        return translate;
    }
}
