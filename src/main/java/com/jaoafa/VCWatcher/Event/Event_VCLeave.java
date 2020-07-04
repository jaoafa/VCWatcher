package com.jaoafa.VCWatcher.Event;

import com.jaoafa.VCWatcher.Main;
import com.jaoafa.VCWatcher.Lib.VCData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_VCLeave {
	@SubscribeEvent
	public void onVCLeave(GuildVoiceLeaveEvent event) {
		System.out.println("onVCLeave()");
		Guild guild = event.getGuild();
		long vcid = event.getChannelLeft().getIdLong();
		long userid = event.getMember().getIdLong();

		if (userid == 357565259151572992L) {
			return;
		}

		long channelid = Main.alertChannel(guild);
		TextChannel channel = Main.getJDA().getTextChannelById(channelid);
		if (channel == null) {
			return;
		}

		Main.setVCData(new VCData(guild.getIdLong(), vcid, userid));
	}
}
