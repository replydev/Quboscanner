package mcping.data;

import com.google.gson.annotations.SerializedName;
import mcping.rawData.*;

public class ForgeResponseOld {

    @SerializedName("description")
    private String description;

    @SerializedName("players")
    private Players players;

    @SerializedName("version")
    private Version version;

    @SerializedName("modinfo")
    private ForgeModInfo modinfo;

    public FinalResponse toFinalResponse(){
        version.setName(version.getName() + " FML with " + modinfo.getNMods() + " mods");
        return new FinalResponse(players,version,"",description);
    }
}