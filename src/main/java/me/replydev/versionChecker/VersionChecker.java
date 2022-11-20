package me.replydev.versionChecker;

import com.google.gson.Gson;
import me.replydev.qubo.Info;
import me.replydev.qubo.gui.MessageWindow;
import me.replydev.utils.Confirm;
import me.replydev.utils.FileUtils;
import me.replydev.utils.Log;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;


public class VersionChecker {

    private final static String url = "https://api.github.com/repos/replydev/QuboScanner/releases/latest";
    private static GithubResponse response;

    public static void checkNewVersion() {
        String remoteString = getGitHubLatest();
        if (remoteString == null) return;

        Version current = new Version(Info.version);
        Version remote = new Version(remoteString);

        if (remote.compareTo(current) > 0) {
            if (Confirm.getConfirm("New version released (" + remoteString + ") would you like to download it?")) {
                try {
                    downloadNewVersion(response.getJarAsset(), remoteString);
                } catch (IOException e) {
                    if (Info.gui)
                        MessageWindow.showMessage("An error occurred during download", e.getMessage());
                    else
                        Log.logln("An error occurred during download: " + e.getMessage());
                    System.exit(-1);
                }
            }
        }
    }

    private static void downloadNewVersion(String surl, String newVersionName) throws IOException {
        URL url = new URL(surl);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream("qubo_" + newVersionName + ".jar");
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        if (Info.gui)
            MessageWindow.showMessage("Download completed", "New binary successfully downloaded! Delete the old one (" + FileUtils.getJarName() + ")");
        else
            Log.logln("New binary successfully downloaded! Delete the old one (" + FileUtils.getJarName() + ").");
        System.exit(-1);
    }

    private static String getGitHubLatest() {
        try {
            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);
            request.addHeader("content-type", "application/json");
            HttpResponse result = httpClient.execute(request);
            String json = EntityUtils.toString(result.getEntity(), "UTF-8");
            response = new Gson().fromJson(json, GithubResponse.class);
            return response.getTag_name();
        } catch (IOException e) {
            return null;
        }
    }
}
