package com.jaoafa.VCWatcher.Event;

import com.jaoafa.VCWatcher.Main;
import com.jaoafa.VCWatcher.Lib.VCData;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_VCMove {
	@SubscribeEvent
	public void onVCMove(GuildVoiceMoveEvent event) {
		System.out.println("onVCMove()");
		Guild guild = event.getGuild();
		String vcName = event.getChannelJoined().getName();
		long vcid = event.getChannelJoined().getIdLong();
		long userid = event.getMember().getIdLong();
		String userstr = event.getMember().getUser().getAsTag();

		if (userid == 357565259151572992L) {
			return;
		}

		if (guild.getAfkChannel().getIdLong() == vcid) {
			return;
		}

		long channelid = Main.alertChannel(guild);
		TextChannel channel = Main.getJDA().getTextChannelById(channelid);
		if (channel == null) {
			return;
		}

		VCData vcdata = Main.getVCData(guild);
		if (vcdata != null) {
			if (vcdata.getLastVCID() == vcid || vcdata.getUserID() == userid) {
				Main.setVCData(new VCData(guild.getIdLong(), vcid, userid));
				return;
			}
		} else {
			Main.setVCData(new VCData(guild.getIdLong(), vcid, userid));
		}

		long count = Main.getVCMembersCountIgnoreBot(event.getChannelJoined());
		if (count != 1) {
			return;
		}

		long lastmessageId = Main.getLastMessageId(guild);
		if (lastmessageId != -1) {
			channel.retrieveMessageById(lastmessageId).queue(
					msg -> {
						if (msg == null) {
							return;
						}
						msg.delete().queue();
					});
		}

		channel.sendMessage(":telephone_receiver:" + vcName + "で" + userstr + "が通話をはじめました。").queue(
				msg -> {
					if (!Main.setLastMessageId(msg.getGuild(), msg.getIdLong())) {
						System.out.println("setLastMessageId: failed.");
					}
				});
	}
}
