package me.replydev.mcping;

import com.google.gson.Gson;
import me.replydev.mcping.LegacyPinger.LegacyPingResponse;
import me.replydev.mcping.data.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class MCPing {
    /**
     * If the client is pinging to determine what version to use, by convention -1 should be set.
     */
    public static final int PROTOCOL_VERSION_DISCOVERY = -1;
    private static final Logger LOGGER = LogManager.getLogger(MCPing.class.getName());

    public ResponseDetails getLegacyPingWithDetails(PingOptions options) throws IOException {
        LegacyPinger pinger = new LegacyPinger();
        pinger.setProtocolVersion(options.getProtocolVersion());
        pinger.setAddress(new InetSocketAddress(options.getHostname(), options.getPort()));
        pinger.setTimeout(options.getTimeout());
        pinger.setProtocolVersion(options.getProtocolVersion());
        LegacyPingResponse response = pinger.ping();
        return new ResponseDetails(response.toFinalResponse(), null, null, null, null, null, null, response, null);
    }

    public ResponseDetails getPingWithDetails(PingOptions options) throws IOException {
        final Gson gson = new Gson();
        Pinger a = new Pinger();
        a.setAddress(new InetSocketAddress(options.getHostname(), options.getPort()));
        a.setTimeout(options.getTimeout());
        a.setProtocolVersion(options.getProtocolVersion());
        String json = a.fetchData();
        try {
            if (json != null) {
                if (json.getBytes(StandardCharsets.UTF_8).length > 5000000) {
                    // got a response greater than 5mb, possible honeypot?
                    LOGGER.error("Got a json response > 5mb, possible honeypot?");
                    LOGGER.error(options.getHostname() + ":" + options.getPort());
                    return null;
                }
                if (json.contains("{")) {
                    if (json.contains("\"modid\"") && json.contains("\"translate\"")) { //it's a forge response translate
                        ForgeResponseTranslate forgeResponseTranslate = gson.fromJson(json, ForgeResponseTranslate.class);
                        return new ResponseDetails(forgeResponseTranslate.toFinalResponse(), forgeResponseTranslate, null, null, null, null, null, null, json);
                    } else if (json.contains("\"modid\"") && json.contains("\"text\"")) { //it's a normal forge response
                        ForgeResponse forgeResponse = gson.fromJson(json, ForgeResponse.class);
                        return new ResponseDetails(forgeResponse.toFinalResponse(), null, forgeResponse, null, null, null, null, null, json);
                    } else if (json.contains("\"modid\"")) {  //it's an old forge response
                        ForgeResponseOld forgeResponseOld = gson.fromJson(json, ForgeResponseOld.class);
                        return new ResponseDetails(forgeResponseOld.toFinalResponse(), null, null, forgeResponseOld, null, null, null, null, json);
                    } else if (json.contains("\"extra\"")) { //it's an extra response
                        ExtraResponse extraResponse = gson.fromJson(json, ExtraResponse.class);
                        return new ResponseDetails(extraResponse.toFinalResponse(), null, null, null, extraResponse, null, null, null, json);
                    } else if (json.contains("\"text\"")) { //it's a new response
                        NewResponse newResponse = gson.fromJson(json, NewResponse.class);
                        return new ResponseDetails(newResponse.toFinalResponse(), null, null, null, null, newResponse, null, null, json);
                    } else { //it's an old response
                        OldResponse oldResponse = gson.fromJson(json, OldResponse.class);
                        return new ResponseDetails(oldResponse.toFinalResponse(), null, null, null, null, null, oldResponse, null, json);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(json);
        }

        return null;
    }

    public FinalResponse getPing(PingOptions options) throws IOException {
        return getPingWithDetails(options).standard;
    }

    public static class ResponseDetails {
        public final FinalResponse standard;
        public final ForgeResponseTranslate forgeTranslate;
        public final ForgeResponse forge;
        public final ForgeResponseOld oldForge;
        public final ExtraResponse extraResponse;
        public final NewResponse response;
        public final OldResponse oldResponse;
        public final LegacyPingResponse legacyResponse;
        public final String json;

        public ResponseDetails(FinalResponse standard,
                               ForgeResponseTranslate forgeTranslate,
                               ForgeResponse forge,
                               ForgeResponseOld oldForge,
                               ExtraResponse extraResponse,
                               NewResponse response,
                               OldResponse oldResponse,
                               LegacyPingResponse legacyResponse,
                               String json) {
            this.standard = standard;
            this.forgeTranslate = forgeTranslate;
            this.forge = forge;
            this.oldForge = oldForge;
            this.extraResponse = extraResponse;
            this.response = response;
            this.oldResponse = oldResponse;
            this.legacyResponse = legacyResponse;
            this.json = json;
        }
    }
}