package mcping;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.google.gson.Gson;
import mcping.data.*;

public class MCPing 
{

    private final static Gson gson = new Gson();

    public FinalResponse getPing(PingOptions options) throws IOException 
    {
        Pinger a = new Pinger();
        a.setAddress(new InetSocketAddress(options.getHostname(),options.getPort()));
        a.setTimeout(options.getTimeout());
        String json = a.fetchData();
        if(json != null){
            if(json.contains("{"))
            {
                if(json.contains("\"modid\"") && json.contains("\"translate\"")){ //it's a forge response translate
                    return gson.fromJson(json, ForgeResponseTranslate.class).toFinalResponse();
                }
                else if(json.contains("\"modid\"") && json.contains("\"text\"")){ //it's a normal forge response
                    return gson.fromJson(json, ForgeResponse.class).toFinalResponse();
                }
                else if(json.contains("\"modid\"")){  //it's an old forge response
                    return gson.fromJson(json, ForgeResponseOld.class).toFinalResponse();
                }
                else if(json.contains("\"extra\"")){ //it's an extra response
                    return gson.fromJson(json,ExtraResponse.class).toFinalResponse();
                }
                else if(json.contains("\"text\"")){ //it's a new response
                    return gson.fromJson(json,NewResponse.class).toFinalResponse();
                }
                else { //it's an old response
                    return gson.fromJson(json,OldResponse.class).toFinalResponse();
                }
            }
        }
        return null;
    }
}