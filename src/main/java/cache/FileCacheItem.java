package cache;

public class FileCacheItem {
    private Long ts;
    private Long expire = 0l;
    private Integer lifeTimeSeconds;

    public Long getTs() {
        return ts;
    }

    public Long getExpire() {
        return expire;
    }

    public Integer getLifeTimeSeconds() {
        return lifeTimeSeconds;
    }

    public FileCacheItem(Long ts, Integer lifeTimeSeconds) {
        this.ts = ts;
        this.lifeTimeSeconds = lifeTimeSeconds;
        this.expire = ts + lifeTimeSeconds * 1000l;
    }

    public FileCacheItem() {

    }
}
