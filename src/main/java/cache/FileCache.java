package cache;

import common.Utils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.ConcurrentHashMap;

public class FileCache {
    private File directory;
    private ConcurrentHashMap<String, FileCacheItem> expires = new ConcurrentHashMap();
    private boolean isAlive = true;

    private void sleep(Long ms){
        try {
            Thread.sleep(ms);
        }
        catch (Exception e){

        }
    }

    private void init() {
        directory.mkdirs();
        try {
            Files.walk(Paths.get(directory.toString()))
                    .filter(Files::isRegularFile)
                    .map(f -> f.getFileName().toFile().getName().split("_"))
                    .filter(f -> f.length == 3)
                    .filter(f -> NumberUtils.isParsable(f[1]))
                    .forEach(parts -> expires.put(parts[0], new FileCacheItem(Long.parseLong(parts[1]), Integer.parseInt(parts[2]))));
        }
        catch (Exception e){

        }

        new Thread(() -> {
            while (isAlive){
                expires.entrySet()
                        .stream()
                        .filter(e -> (e.getValue().getExpire() + 1000l) < System.currentTimeMillis())
                        .forEach(e -> {
                            expires.remove(e.getKey());
                            getFile(e.getKey(), e.getValue()).delete();
                        });
                sleep(60000l);
            }
        }).start();
    }

    public FileCache(String path) {
        directory = new File(path);
        init();
    }

    private File getFile(String innerKey, FileCacheItem item){
        return new File(directory, innerKey + "_" + item.getTs() + "_" + item.getLifeTimeSeconds());
    }

    public void put(String key, String value, Integer liveSeconds) {
        String innerKey = Utils.md5(key);

        FileCacheItem fileCacheItem = new FileCacheItem(System.currentTimeMillis(), liveSeconds);
        expires.put(innerKey, fileCacheItem);

        try {
            Utils.saveData(value, getFile(innerKey, fileCacheItem));
        }
        catch (Exception e){

        }
    }

    public String get(String key) {
        return get(key, null);
    }

    public String get(String key, Integer seconds) {
        String innerKey = Utils.md5(key);
        FileCacheItem item = expires.get(innerKey);

        if(item == null){
            return null;
        }

        String data = null;

        boolean isExpiredGlobal = System.currentTimeMillis() > item.getExpire();
        boolean isExpiredLocal = seconds == null ? false : System.currentTimeMillis() > (item.getTs() + seconds * 1000l);

        if(!isExpiredGlobal && !isExpiredLocal){
            try {
                data =  Utils.readFileAsString(getFile(innerKey, item).toString());
            }
            catch (Exception e){

            }
        }

        return data;
    }

    public void close(){
        isAlive = false;
    }

    public Integer getSize(){
        return expires.size();
    }

}
