package com.jaoafa.VCWatcher;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jaoafa.VCWatcher.Event.Event_VCJoin;
import com.jaoafa.VCWatcher.Event.Event_VCLeave;
import com.jaoafa.VCWatcher.Event.Event_VCMove;
import com.jaoafa.VCWatcher.Lib.VCData;
import net.dv8tion.jda.api.AccountType;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import org.json.JSONObject;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    private static JDA jda = null;

    public static void main(String[] args) {
        File f = new File("conf.properties");
        Properties props;
        try {
            InputStream is = new FileInputStream(f);

            // プロパティファイルを読み込む
            props = new Properties();
            props.load(is);
        } catch (FileNotFoundException e) {
            // ファイル生成
            props = new Properties();
            props.setProperty("token", "PLEASETOKEN");
            try {
                props.store(new FileOutputStream("conf.properties"), "Comments");
                System.out.println("Please Config Token!");
            } catch (IOException e1) {
                e1.printStackTrace();
                return;
            }
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        // キーを指定して値を取得する
        String token = props.getProperty("token");
        if (token.equalsIgnoreCase("PLEASETOKEN")) {
            System.out.println("Please Token!");
            return;
        }

        // 分けてイベント自動登録できるように？
        // 全部JDA移行
        try {
            JDABuilder jdabuilder = new JDABuilder(AccountType.BOT)
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .setToken(token)
                    .setContextEnabled(false)
                    .setEventManager(new AnnotatedEventManager());

            jdabuilder.addEventListeners(new Event_VCJoin());
            jdabuilder.addEventListeners(new Event_VCLeave());
            jdabuilder.addEventListeners(new Event_VCMove());

            jda = jdabuilder.build().awaitReady();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        Runtime.getRuntime().addShutdownHook(
                new Thread(
                        () -> System.out.println("Exit")));
    }

    public static JDA getJDA() {
        return jda;
    }

    public static VCData getVCData(Guild guild) {
        Gson gson = new Gson();
        File file = new File("vcdata.json");
        Set<VCData> vcdatas;
        try {
            Type Settypes = new TypeToken<HashSet<VCData>>() {
            }.getType();
            vcdatas = gson.fromJson(new FileReader(file), Settypes);
            if (vcdatas == null) {
                vcdatas = new HashSet<>();
            }
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            return null;
        }
        List<VCData> filtered = vcdatas.stream().filter(one -> one != null && one.getGuildID() == guild.getIdLong())
                .collect(Collectors.toList());
        if (!filtered.isEmpty()) {
            return filtered.get(0);
        }
        return null;
    }

    public static void setVCData(VCData vcdata) {
        Gson gson = new Gson();
        File file = new File("vcdata.json");
        Set<VCData> vcdatas;
        try {
            Type Settypes = new TypeToken<HashSet<VCData>>() {
            }.getType();
            vcdatas = gson.fromJson(new FileReader(file), Settypes);
            if (vcdatas == null) {
                vcdatas = new HashSet<>();
            }
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            vcdatas = new HashSet<>();
        }
        List<VCData> filtered = vcdatas.stream().filter(one -> one != null && one.getGuildID() == vcdata.getGuildID())
                .collect(Collectors.toList());
        if (!filtered.isEmpty()) {
            vcdatas.remove(filtered.get(0));
        }
        vcdatas.add(vcdata);
        try {
            System.out.println(gson.toJson(vcdatas));
            Writer writer = new FileWriter(file);
            gson.toJson(vcdatas, writer);
            writer.flush();
            writer.close();
        } catch (JsonIOException | IOException e) {
            e.printStackTrace();
        }
    }

    public static long alertChannel(Guild guild) {
        Gson gson = new Gson();
        File file = new File("alertchannel.json");
        try {
            Type Maptype = new TypeToken<HashMap<String, Long>>() {
            }.getType();
            Map<String, Long> map = gson.fromJson(new FileReader(file), Maptype);
            if (!map.containsKey(guild.getId())) {
                return -1;
            }
            return map.get(guild.getId());
        } catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
            return -1;
        }
    }

    public static long getLastMessageId(Guild guild) {
        try {
            String data = String.join("\n", Files.readAllLines(Paths.get("lastmessage.json")));
            JSONObject json = new JSONObject(data);
            if (!json.has(guild.getId())) {
                return -1;
            }
            return json.getLong(guild.getId());
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean setLastMessageId(Guild guild, long messageid) {
        try {
            Path path = Paths.get("lastmessage.json");
            String data = String.join("\n", Files.readAllLines(path));
            JSONObject json = new JSONObject(data);
            json.put(guild.getId(), messageid);
            Files.write(path, json.toString().getBytes(), StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static long getVCMembersCountIgnoreBot(VoiceChannel channel) {
        return channel.getMembers().stream().filter(member -> !member.getUser().isBot()).count();
    }
}
