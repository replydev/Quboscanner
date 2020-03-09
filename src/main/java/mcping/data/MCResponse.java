package mcping.data;

import com.google.gson.annotations.SerializedName;
import mcping.rawData.Players;
import mcping.rawData.Version;

class MCResponse {
    @SerializedName("players")
    Players players;
    @SerializedName("version")
    Version version;
    @SerializedName("favicon")
    String favicon;
}
