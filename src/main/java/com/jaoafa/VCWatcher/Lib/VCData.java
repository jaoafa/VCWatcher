package com.jaoafa.VCWatcher.Lib;

public class VCData {
	private long guildid = -1;
	private long lastvcid = -1;
	private long userid = -1;

	public VCData(long guildid, long lastvcid, long userid) {
		this.guildid = guildid;
		this.lastvcid = lastvcid;
		this.userid = userid;
	}

	public long getGuildID() {
		return guildid;
	}

	public long getLastVCID() {
		return lastvcid;
	}

	public long getUserID() {
		return userid;
	}

	public void setGuildID(long guildid) {
		this.guildid = guildid;
	}

	public void setLastVCID(long lastvcid) {
		this.lastvcid = lastvcid;
	}

	public void setUserID(long userid) {
		this.userid = userid;
	}

}
