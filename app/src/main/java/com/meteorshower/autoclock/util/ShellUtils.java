package com.meteorshower.autoclock.util;

import android.text.TextUtils;
import android.util.Log;

import com.meteorshower.autoclock.constant.Constant;

import org.apache.commons.io.FileUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShellUtils {

    public static Process cmd(String command) {
        return process("adb " + command);
    }

    public static Process shell(String command) {
        return process("adb shell " + command);
    }

    public static BufferedReader shellOut(Process ps) {
        BufferedInputStream in = new BufferedInputStream(ps.getInputStream());
        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        return br;
    }

    public static String getShellOut(Process ps) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = shellOut(ps);
        String line;

        try {
            while ((line = br.readLine()) != null) {
                // sb.append(line);
                sb.append(line + System.getProperty("line.separator"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString().trim();
    }

    // 返回的是List类型
    public static List<String> getShellOut2(Process ps) {
        List<String> list = new ArrayList<>();
        BufferedReader br = shellOut(ps);
        String line;

        try {
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static Process process(String command) {
        Process ps = null;
        try {
            Log.i("adb", command);
            ps = Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ps;
    }

    /**
     * 执行su命令，最多重复执行5次
     */
    public static String performSuCommandAndGetRes(String cmd)
            throws InterruptedException {
        int repeatTimes = 5;
        for (int i = 0; i < repeatTimes; i++) {
            String result = "";
            DataOutputStream dos = null;
            DataInputStream dis = null;
            try {
                Process p = Runtime.getRuntime().exec("su");
                dos = new DataOutputStream(p.getOutputStream());
                dis = new DataInputStream(p.getInputStream());
                dos.writeBytes(cmd + "; echo \"suCmdRes=\"$?\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();

                String line = "";
                BufferedReader din = new BufferedReader(new InputStreamReader(
                        dis));
                while ((line = din.readLine()) != null) {
                    result += line;
                }
                p.waitFor();
            } catch (Exception e) {
                Util.saveThrowableInfo(e);
                e.printStackTrace();
            } finally {
                if (dos != null) {
                    try {
                        dos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.i("sudo", cmd + " = " + result);
            if (result != null && result.contains("suCmdRes") == true) {
                for (int j = -5; j < 5; j++) {
                    result = result.replace("suCmdRes=" + j, "");
                }
                return result;
            } else {
                if (i == repeatTimes - 1)
                    break;
                Thread.sleep(1500);
            }
        }

        return null;
    }

    /**
     * 执行su命令，最多重复执行5次
     */
    public static boolean performSuCommand(String cmd)
            throws InterruptedException {
        int repeatTimes = 5;
        for (int i = 0; i < repeatTimes; i++) {
            String result = "";
            DataOutputStream dos = null;
            DataInputStream dis = null;
            try {
                Process p = Runtime.getRuntime().exec("su");
                dos = new DataOutputStream(p.getOutputStream());
                dis = new DataInputStream(p.getInputStream());
                dos.writeBytes(cmd + "; echo \"suCmdRes=\"$?\n");
                dos.flush();
                dos.writeBytes("exit\n");
                dos.flush();

                String line = "";
                BufferedReader din = new BufferedReader(new InputStreamReader(
                        dis));
                while ((line = din.readLine()) != null) {
                    result += line;
                }
                p.waitFor();
            } catch (Exception e) {
                Log.d(Constant.TAG, "performSuCommand error = ", e);
            } finally {
                if (dos != null) {
                    try {
                        dos.close();
                    } catch (IOException e) {
                        Log.d(Constant.TAG, "performSuCommand error = ", e);
                    }
                }
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException e) {
                        Log.d(Constant.TAG, "performSuCommand error = ", e);
                    }
                }
            }

            Log.d(Constant.TAG, cmd + " = " + result);
            if (result != null && result.contains("suCmdRes=0") == true) {
                return true;
            } else {
                if (i == repeatTimes - 1)
                    break;
                Thread.sleep(1500);
            }
        }

        return false;
    }

    /**
     * 安装APK
     */
    public static boolean installUseRoot(String filePath)
            throws InterruptedException {
        if (TextUtils.isEmpty(filePath))
            throw new IllegalArgumentException("Please check apk file path!");
        String command = "pm install -r " + filePath;
        return performSuCommand(command);
    }

    /**
     * 用su命令拷贝文件到指定文件夹
     */
    public static boolean cpFile(String filePath, String targetPath)
            throws InterruptedException {
        String command = "cp -f " + filePath + " " + targetPath;
        return performSuCommand(command);
    }

    public static boolean cpDir(String src, String dst)
            throws InterruptedException {
        String command = "cp -rf " + src + " " + dst;
        return performSuCommand(command);
    }

    /**
     * 删除文件
     */
    public static boolean rmFile(String path) throws InterruptedException {
        String command = "rm -f " + path;
        return performSuCommand(command);
    }

    /**
     * 删除目录
     */
    public static boolean rmDir(String path, boolean rmDir)
            throws InterruptedException {
        String command = "";
        if (rmDir == false) {
            if (path.endsWith("/"))
                command = "rm -rf " + path + "*";
            else
                command = "rm -rf " + path + "/*";
        } else {
            command = "rm -rf " + path;
        }

        return performSuCommand(command);
    }

    public static boolean performSuCmdSh(String shPath, String cmd)
            throws IOException, InterruptedException {
        if (shPath == null || cmd == null)
            return false;

        File sh = new File(shPath);
        if (!sh.exists()) {
            if (!sh.createNewFile())
                return false;
        }

        FileUtils.writeStringToFile(sh, cmd, "utf8");
        return (performSuCommand("chmod 777 " + shPath) && performSuCommand((shPath
                .startsWith("/") ? "." : "./") + shPath));
    }


    public static final String COMMAND_SU       = "su";
    public static final String COMMAND_SH       = "sh";
    public static final String COMMAND_EXIT     = "exit\n";
    public static final String COMMAND_LINE_END = "\n";

    private ShellUtils() {
        throw new AssertionError();
    }

    /**
     * check whether has root permission
     *
     * @return
     */
    public static boolean checkRootPermission() {
        return execCommand("echo root", true, false).result == 0;
    }

    /**
     * execute shell command, default return result msg
     *
     * @param command command
     * @param isRoot whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot) {
        return execCommand(new String[] {command}, isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     *
     * @param commands command list
     * @param isRoot whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, true);
    }

    /**
     * execute shell commands, default return result msg
     *
     * @param commands command array
     * @param isRoot whether need to run with root
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot) {
        return execCommand(commands, isRoot, true);
    }

    /**
     * execute shell command
     *
     * @param command command
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(String command, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(new String[] {command}, isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands command list
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return
     * @see ShellUtils#execCommand(String[], boolean, boolean)
     */
    public static CommandResult execCommand(List<String> commands, boolean isRoot, boolean isNeedResultMsg) {
        return execCommand(commands == null ? null : commands.toArray(new String[] {}), isRoot, isNeedResultMsg);
    }

    /**
     * execute shell commands
     *
     * @param commands command array
     * @param isRoot whether need to run with root
     * @param isNeedResultMsg whether need result msg
     * @return <ul>
     *         <li>if isNeedResultMsg is false, {@link CommandResult#successMsg} is null and
     *         {@link CommandResult#errorMsg} is null.</li>
     *         <li>if {@link CommandResult#result} is -1, there maybe some excepiton.</li>
     *         </ul>
     */
    public static CommandResult execCommand(String[] commands, boolean isRoot, boolean isNeedResultMsg) {
        int result = -1;
        if (commands == null || commands.length == 0) {
            return new CommandResult(result, null, null);
        }

        Process process = null;
        BufferedReader successResult = null;
        BufferedReader errorResult = null;
        StringBuilder successMsg = null;
        StringBuilder errorMsg = null;

        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec(isRoot ? COMMAND_SU : COMMAND_SH);
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                // donnot use os.writeBytes(commmand), avoid chinese charset error
                os.write(command.getBytes());
                os.writeBytes(COMMAND_LINE_END);
                os.flush();
            }
            os.writeBytes(COMMAND_EXIT);
            os.flush();

            result = process.waitFor();
            // get command result
            if (isNeedResultMsg) {
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String s;
                while ((s = successResult.readLine()) != null) {
                    successMsg.append(s);
                }
                while ((s = errorResult.readLine()) != null) {
                    errorMsg.append(s);
                }
            }
        } catch (IOException e) {
            Log.d("lqwtest", "execommond error = ",e);
        } catch (Exception e) {
            Log.d("lqwtest", "execommond error = ",e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (successResult != null) {
                    successResult.close();
                }
                if (errorResult != null) {
                    errorResult.close();
                }
            } catch (IOException e) {
                Log.d("lqwtest", "execommond error = ",e);
            }

            if (process != null) {
                process.destroy();
            }
        }
        return new CommandResult(result, successMsg == null ? null : successMsg.toString(), errorMsg == null ? null
                : errorMsg.toString());
    }

    /**
     * result of command
     * <ul>
     * <li>{@link CommandResult#result} means result of command, 0 means normal, else means error, same to excute in
     * linux shell</li>
     * <li>{@link CommandResult#successMsg} means success message of command result</li>
     * <li>{@link CommandResult#errorMsg} means error message of command result</li>
     * </ul>
     *
     * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2013-5-16
     */
    public static class CommandResult {

        /** result of command **/
        public int    result;
        /** success message of command result **/
        public String successMsg;
        /** error message of command result **/
        public String errorMsg;

        public CommandResult(int result) {
            this.result = result;
        }

        public CommandResult(int result, String successMsg, String errorMsg) {
            this.result = result;
            this.successMsg = successMsg;
            this.errorMsg = errorMsg;
        }

        @Override
        public String toString() {
            return "CommandResult{" +
                    "result=" + result +
                    ", successMsg='" + successMsg + '\'' +
                    ", errorMsg='" + errorMsg + '\'' +
                    '}';
        }
    }
}
