package mcping.data;

import com.google.gson.annotations.SerializedName;
import mcping.rawData.Description;
import mcping.rawData.Players;
import mcping.rawData.Version;

public class NewResponse extends MCResponse {

    @SerializedName("description")
    private final Description description;


    public void setVersion(String a){
        version.setName(a);
    }

    public NewResponse(){
        description = new Description();
        players = new Players();
        version = new Version();
    }

    public Description getDescription() {
        return this.description;
    }

    public FinalResponse toFinalResponse(){
        return new FinalResponse(players,version,favicon,description.getText());
    }

}
