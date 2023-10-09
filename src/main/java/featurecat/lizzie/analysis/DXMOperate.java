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
    static String infoMove = null;
    static Timer timer = null;

    public static String operateOutCommand(String command) {
        if (command.indexOf("startGame") != -1) {
            //分析模式不用startGame 对其他没有影响dongxiaoming
//            分析模式和genmove模式发现胜率图跳动的问题 是两种命令不同导致
//            所以这个类实现了强转统一分析模式， 和手动分析同一种命令 这样胜率图就不会较大的变动
//            然后发现即使是分析命令 胜率图也会较大的变动 最后定位在startGame命令
//            使用分析模式时要去掉startGame命令
            return "name";
        }

//        if (true) {
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
                timer = null;
            }
        }
        System.out.println("输出-->" + command + "\n");
        commandSave = command;
        return command;
    }


    public static String operateInResult(String result, BufferedOutputStream outputStream) {
        System.out.println("返回<---" + result);
//        if (true) {
//            return result;
//        }
        if (Lizzie.frame.isAiPlaying() && result.startsWith("info move") && commandOrgan.startsWith("kata-genmove_analyze")) {
            infoMove = result;
            if (timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            String temp = infoMove.replaceAll("info move ", "");
                            infoMove = null;
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
                            Lizzie.leelaz.ad(position);
                            outputStream.write("stop\n".getBytes());
                            outputStream.flush();
                            timer.cancel();
                            if(Lizzie.leelaz.isPondering()){
                                if (Lizzie.board.getHistory().isBlacksTurn()) {
                                    String command = "kata-analyze b 10";
                                    outputStream.write((command + "\n").getBytes());
                                } else {
                                    String command = "kata-analyze w 10";
                                    outputStream.write((command + "\n").getBytes());
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }, 5000, 10000);
            }
        }
        return result;
    }

}
