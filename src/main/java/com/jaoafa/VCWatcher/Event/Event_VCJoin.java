package com.jaoafa.VCWatcher.Event;

import com.jaoafa.VCWatcher.Lib.VCData;
import com.jaoafa.VCWatcher.Main;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

public class Event_VCJoin {
    @SubscribeEvent
    public void onVCJoin(GuildVoiceJoinEvent event) {
        System.out.println("onVCJoin()");
        Guild guild = event.getGuild();
        String vcName = event.getChannelJoined().getName();
        long vcid = event.getChannelJoined().getIdLong();
        long userid = event.getMember().getIdLong();
        String userstr = event.getMember().getUser().getAsTag();

        if (userid == 357565259151572992L) {
            return;
        }

        long channelid = Main.alertChannel(guild);
        TextChannel channel = Main.getJDA().getTextChannelById(channelid);
        if (channel == null) {
            return;
        }

        if (guild.getAfkChannel() != null && guild.getAfkChannel().getIdLong() == vcid) {
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
        if (lastmessageId != -1 && channel.hasLatestMessage()) {
            long nowLastMsgID = channel.getLatestMessageIdLong();
            if (nowLastMsgID != lastmessageId) {
                channel.retrieveMessageById(lastmessageId).queue(
                        msg -> {
                            if (msg == null) {
                                return;
                            }
                            msg.delete().queue();
                        });
                lastmessageId = -1;
            }
        }

        if (lastmessageId != -1) {
            channel.sendMessage(":telephone_receiver:" + vcName + "で" + userstr + "が通話をはじめました。").queue(
                    msg -> {
                        if (!Main.setLastMessageId(msg.getGuild(), msg.getIdLong())) {
                            System.out.println("setLastMessageId: failed.");
                        }
                    });
        } else {
            channel.retrieveMessageById(lastmessageId).queue(msg -> msg.editMessage(":telephone_receiver:" + vcName + "で" + userstr + "が通話をはじめました。").queue(
                    _msg -> {
                        if (!Main.setLastMessageId(_msg.getGuild(), _msg.getIdLong())) {
                            System.out.println("setLastMessageId: failed.");
                        }
                    }),
                    failure -> channel.sendMessage(":telephone_receiver:" + vcName + "で" + userstr + "が通話をはじめました。").queue(
                            msg -> {
                                if (!Main.setLastMessageId(msg.getGuild(), msg.getIdLong())) {
                                    System.out.println("setLastMessageId: failed.");
                                }
                            }));
        }
    }
}
