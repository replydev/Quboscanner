package versionChecker;

public class GithubResponse {

    private String tag_name;

    public String getTag_name() {
        return tag_name;
    }

    private Asset[] assets;

    public String getJarAsset(){
        for(Asset as : assets){
            if(as.getBrowser_download_url().endsWith(".jar")) return as.getBrowser_download_url();
        }
        return null;
    }
}

class Asset {

    private String browser_download_url;

    public String getBrowser_download_url() {
        return browser_download_url;
    }
}