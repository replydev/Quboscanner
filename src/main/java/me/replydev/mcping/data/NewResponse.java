package me.replydev.mcping.data;

import com.google.gson.annotations.SerializedName;
import me.replydev.mcping.rawData.Description;
import me.replydev.mcping.rawData.Players;
import me.replydev.mcping.rawData.Version;

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
