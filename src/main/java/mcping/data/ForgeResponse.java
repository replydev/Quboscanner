package mcping.data;

import com.google.gson.annotations.SerializedName;
import mcping.rawData.ForgeDescription;
import mcping.rawData.ForgeModInfo;
import mcping.rawData.Players;
import mcping.rawData.Version;

public class ForgeResponse {

    @SerializedName("description")
    private ForgeDescription description;

    @SerializedName("players")
    private Players players;

    @SerializedName("version")
    private Version version;

    @SerializedName("modinfo")
    private ForgeModInfo modinfo;

    public FinalResponse toFinalResponse(){
        version.setName(version.getName() + " FML with " + modinfo.getNMods() + " mods");
        return new FinalResponse(players,version,"",description.getTranslate());
    }
}
