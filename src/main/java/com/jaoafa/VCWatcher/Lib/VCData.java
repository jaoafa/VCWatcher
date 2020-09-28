package com.jaoafa.VCWatcher.Lib;

public class VCData {
    private long guildid;
    private long lastvcid;
    private long userid;

    public VCData(long guildid, long lastvcid, long userid) {
        this.guildid = guildid;
        this.lastvcid = lastvcid;
        this.userid = userid;
    }

    public long getGuildID() {
        return guildid;
    }

    public void setGuildID(long guildid) {
        this.guildid = guildid;
    }

    public long getLastVCID() {
        return lastvcid;
    }

    public void setLastVCID(long lastvcid) {
        this.lastvcid = lastvcid;
    }

    public long getUserID() {
        return userid;
    }

    public void setUserID(long userid) {
        this.userid = userid;
    }

}
