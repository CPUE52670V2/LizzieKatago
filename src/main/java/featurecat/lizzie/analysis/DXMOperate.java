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
//        if (command.indexOf("startGame") != -1) {
//            return "";
//        }
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
        System.out.println("输出-->" + command + "\n");
        commandSave = command;
        return command;
    }

    static String infoMove = null;
    static Timer timer =null;
    public static String operateInResult(String result, BufferedOutputStream outputStream) {
        System.out.println("返回<---" + result);
//        if(true){
//            return result;
//        }
//        map.put("key", result);
        if (result.indexOf("=") != -1&&infoMove!=null) {
            if(timer!=null){
                timer.cancel();
                timer=null;
            }
            String temp = infoMove.replaceAll("info move ", "");
            infoMove=null;
            int i = temp.indexOf("visits");
            String po = temp.substring(0, i - 1);
            if (Lizzie.board.getHistory().isBlacksTurn()) {
                temp = "play B " + po;
                position = "play " + po;
            } else {
                temp = "play W " + po;
                position = "play " + po;
            }
            try {
                outputStream.write((temp + "\n").getBytes());
                Lizzie.leelaz.ad(position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Lizzie.frame.isAiPlaying() && result.startsWith("info move") && commandOrgan.startsWith("kata-genmove_analyze")) {
            infoMove = result;
            if(timer==null){
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            outputStream.write("stop\n".getBytes());
                            outputStream.flush();
                            timer.cancel();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                },5000,10000);

            }

        }
        return result;

    }

}
