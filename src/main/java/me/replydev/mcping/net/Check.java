package me.replydev.mcping.net;

import com.google.gson.JsonSyntaxException;
import me.replydev.mcping.MCPing;
import me.replydev.mcping.PingOptions;
import me.replydev.mcping.data.FinalResponse;
import me.replydev.qubo.Info;
import me.replydev.qubo.QuboInstance;
import me.replydev.qubo.gui.MainWindow;
import me.replydev.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Check implements Runnable {

    private final String hostname;
    private final int port;
    private final String filename;
    private final int timeout;
    private final int count;
    private final QuboInstance quboInstance;
    private final String filterVersion;
    private final String filterMotd;
    private final int minPlayer;

    public Check(String hostname, int port, int timeout, String filename, int count, QuboInstance quboInstance, String filterVersion, String filterMotd, int minPlayer) {
        this.hostname = hostname;
        this.port = port;
        this.filename = filename;
        this.timeout = timeout;
        this.count = count;
        this.quboInstance = quboInstance;
        this.filterVersion = filterVersion;
        this.filterMotd = filterMotd;
        this.minPlayer = minPlayer;
    }

    private static String getGoodDescription(String des) {  //ritorna la stringa senza spazi multipli e senza §
        if (des == null) return "";
        des = des.replace("§0", "");
        des = des.replace("§1", "");
        des = des.replace("§2", "");
        des = des.replace("§3", "");
        des = des.replace("§4", "");
        des = des.replace("§5", "");
        des = des.replace("§6", "");
        des = des.replace("§7", "");
        des = des.replace("§8", "");
        des = des.replace("§9", "");
        des = des.replace("§a", "");
        des = des.replace("§b", "");
        des = des.replace("§c", "");
        des = des.replace("§d", "");
        des = des.replace("§e", "");
        des = des.replace("§f", "");
        des = des.replace("§l", "");
        des = des.replace("§m", "");
        des = des.replace("§n", "");
        des = des.replace("§o", "");
        des = des.replace("§r", "");
        des = des.trim().replaceAll(" +", " "); //rimuove spazi multipli
        des = des.replace("\n", "");
        return des;
    }

    public void run() {
        check();
        this.quboInstance.currentThreads.decrementAndGet();
    }

    private void check() {
        if (hostname == null || filterVersion == null || filterMotd == null) return;

        for (int i = 0; i < count; i++) {
            try {
                long time = System.currentTimeMillis();
                try {
                    FinalResponse response = new MCPing().getPing(new PingOptions().setHostname(hostname).setPort(port).setTimeout(timeout));
                    if (response == null) continue;
                    if (response.getDescription().contains(filterMotd) && response.getVersion().getName().contains(filterVersion) && response.getPlayers().getOnline() > minPlayer) {
                        String des = getGoodDescription(response.getDescription());
                        String dati = "-----------------------\n" + hostname + ":" + port +
                                "\nVersion: " + response.getVersion().getName() + "\n" +
                                "Online: " + response.getPlayers().getOnline() + "/" + response.getPlayers().getMax() + "\n" +
                                "MOTD: " + des + "\n" +
                                "Ping time: " + (System.currentTimeMillis() - time) + " ms";
                        String singleLine = "(" + hostname + ":" + port + ")(" + response.getPlayers().getOnline() + "/" + response.getPlayers().getMax() + ")" + "(" + response.getVersion().getName() + ")" + "(" + des + ")";
                        Info.serverFound++;
                        Info.serverNotFilteredFound++;
                        if (Info.gui) {
                            MainWindow.dtm.addRow(new Object[]{Info.serverFound, hostname, port, response.getPlayers().getOnline() + "/" + response.getPlayers().getMax(), response.getVersion().getName(), des});
                        } else System.out.println(singleLine);

                        if (quboInstance.inputData.isOutput() && Files.exists(Paths.get(filename))) {
                            if (quboInstance.inputData.isFulloutput()) {
                                FileUtils.appendToFile(dati, filename);
                            } else FileUtils.appendToFile(singleLine, filename);
                        }
                    } else Info.serverNotFilteredFound++;
                    return;
                } catch (JsonSyntaxException e) {
                    System.out.println("(" + hostname + ":" + port + ")(Json not readable)");
                    if (quboInstance.inputData.isOutput())
                        FileUtils.appendToFile("-----------------------" + hostname + ":" + port + "\nJson not readable", filename);
                    Info.serverNotFilteredFound++;
                } catch (NullPointerException e) {
                    if (this.quboInstance.inputData.isDebugMode())
                        System.out.println("WARN: NullPointerException for: " + hostname + ":" + port);
                }
            } catch (IOException ignored) {
            }
        }
    }
}