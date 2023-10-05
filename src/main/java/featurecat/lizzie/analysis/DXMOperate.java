package featurecat.lizzie.analysis;

import featurecat.lizzie.Lizzie;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

/**
 * @author dongxiaoming.
 * @date 2023/10/5 13:55.
 */
public class DXMOperate {
    public static String commandOrgan = "";
    public static String commandSave = "";
    public static String position = "";

    public static String operateOutCommand(String command) {
        if (command.indexOf("startGame") != -1) {
            return "";
        }
        System.out.println("输出-->" + command + "\n");
//        if(true){
//            return command;
//        }
        commandOrgan = command;
        if (Lizzie.frame.isAiPlaying()) {
            if (command.startsWith("kata-genmove_analyze")) {
                if (Lizzie.board.getHistory().isBlacksTurn()) {
                    command = "kata-analyze b 10";
                } else {
                    command = "kata-analyze w 10";
                }
            }
        }
        commandSave = command;
        return command;
    }

    static Map<String, String> map = new HashMap();

    public static String operateInResult(String result, BufferedOutputStream outputStream) {
        System.out.println("返回<---" + result);
//        if(true){
//            return result;
//        }
        map.put("key", result);
        if (Lizzie.frame.isAiPlaying() && result.startsWith("info move") && commandOrgan.startsWith("kata-genmove_analyze")) {
            commandOrgan = "";
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    try {
                        String temp = s.get("key").replaceAll("info move ", "");
                        int i = temp.indexOf("visits");
                        String po = temp.substring(0, i - 1);
                        if (Lizzie.board.getHistory().isBlacksTurn()) {
                            temp = "play B " + po;
                            position = "play " + po;
                        } else {
                            temp = "play W " + po;
                            position = "play " + po;
                        }
                        outputStream.write((temp + "\n").getBytes());
                        outputStream.write("stop\n".getBytes());
                        outputStream.flush();

                        Lizzie.leelaz.ad(position);
                        timer.cancel();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 5000, 100000);
        }
        return result;

    }

}
