package me.replydev.mcping.data;

import com.google.gson.annotations.SerializedName;
import me.replydev.mcping.rawData.ExtraDescription;

public class ExtraResponse extends MCResponse {

    private static final String PAPER_VERSION_PREFIX = "Paper ";
    private static final String SPIGOT_VERSION_PREFIX = "Spigot ";
    private static final String VELOCITY_VERSION_PREFIX = "Velocity ";
    private static final String BUNGEE_VERSION_PREFIX = "BungeeCord ";
    private static final String WATERFALL_VERSION_PREFIX = "Waterfall ";
    private static final String TACO_SPIGOT_VERSION_PREFIX = "TacoSpigot ";
    private static final String CRAFT_BUKKIT_VERSION_PREFIX = "CraftBukkit ";
    private static final String MOHIST_VERSION_PREFIX = "Mohist ";
    private static final String PURPUR_VERSION_PREFIX = "Purpur ";
    private static final String SPORTPAPER_VERSION_PREFIX = "SportPaper ";
    private static final String FLAMECORD_VERSION_PREFIX = "FlameCord ";

    @SerializedName("description")
    private ExtraDescription description;

    private Loader loader;

    public FinalResponse toFinalResponse() {
        if (version != null && version.getName().startsWith(SPIGOT_VERSION_PREFIX)) {
            loader = Loader.SPIGOT;
            version.setName(version.getName().substring(SPIGOT_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(VELOCITY_VERSION_PREFIX)) {
            loader = Loader.VELOCITY;
            version.setName(version.getName().substring(VELOCITY_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(BUNGEE_VERSION_PREFIX)) {
            loader = Loader.BUNGEE;
            version.setName(version.getName().substring(BUNGEE_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(WATERFALL_VERSION_PREFIX)) {
            loader = Loader.WATERFALL;
            version.setName(version.getName().substring(WATERFALL_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(PAPER_VERSION_PREFIX)) {
            loader = Loader.PAPER;
            version.setName(version.getName().substring(PAPER_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(TACO_SPIGOT_VERSION_PREFIX)) {
            loader = Loader.TACO_SPIGOT;
            version.setName(version.getName().substring(TACO_SPIGOT_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(CRAFT_BUKKIT_VERSION_PREFIX)) {
            loader = Loader.CRAFT_BUKKIT;
            version.setName(version.getName().substring(CRAFT_BUKKIT_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(MOHIST_VERSION_PREFIX)) {
            loader = Loader.MOHIST;
            version.setName(version.getName().substring(MOHIST_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(PURPUR_VERSION_PREFIX)) {
            loader = Loader.PURPUR;
            version.setName(version.getName().substring(PURPUR_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(SPORTPAPER_VERSION_PREFIX)) {
            loader = Loader.SPORTPAPER;
            version.setName(version.getName().substring(SPORTPAPER_VERSION_PREFIX.length()).trim());
        } else if (version != null && version.getName() != null && version.getName().startsWith(FLAMECORD_VERSION_PREFIX)) {
            loader = Loader.FLAMECORD;
            version.setName(version.getName().substring(FLAMECORD_VERSION_PREFIX.length()).trim());
        } else {
            loader = Loader.VANILLA;
        }
        return new FinalResponse(players, version, favicon, description.getText());
    }

    public Loader getLoader() {
        return loader;
    }
}
