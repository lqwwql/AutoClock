package com.meteorshower.autoclock.view;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.constant.Constant;
import com.meteorshower.autoclock.http.SocketClient;
import com.meteorshower.autoclock.util.AccessibilityUtils;

import butterknife.BindView;
import butterknife.OnClick;
/**
 * 发布shell命令页面
 * */
public class CommandExecuteActivity extends BasicActivity {

    @BindView(R.id.et_command)
    EditText etCommand;
    @BindView(R.id.tv_exc_result)
    TextView tvExcResult;

    @Override
    protected int getLayoutID() {
        return R.layout.activity_command_excute;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {

    }

    @OnClick({R.id.btn_exc_command})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_exc_command:
                String command = getCommand(etCommand.getText().toString().trim());
                excCommand(command);
                break;
            default:
                break;
        }
    }

    private void excCommand(final String command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new SocketClient(command, new SocketClient.CmdExcSendListener() {
                    @Override
                    public void getExcResult(String result) {
                        setTvExcResult(result);
                    }
                });
            }
        }).start();
    }

    private void setTvExcResult(final String result) {
        CommandExecuteActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvExcResult.setText(result);
            }
        });
    }

    private String getCommand(String input) {
        Log.d(Constant.TAG, "input = " + input);
        String command = "";
        switch (input) {
            case "1":
                command = Constant.THREAD_IS_START_COMMAND;
                break;
            case "2":
                command = Constant.MAIN_THREAD_RUNNING_CHANGE;
                break;
            case "3":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(152, 1043, 10));
                break;
            case "4":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(550, 1716, 10));
                break;
            case "5":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(142, 1295, 10));
                break;
            case "6":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(540, 709, 10));
                break;
            case "7":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getGoBackCommand();
                break;
            case "8":
                try {
                    Thread.sleep(5 * 1000);
                } catch (InterruptedException e) {
                }
                command = AccessibilityUtils.getClickCommand(AccessibilityUtils.getClickRect(113, 1717, 10));
                break;
        }
        Log.d(Constant.TAG, "command = " + command);
        return command;
    }
}
