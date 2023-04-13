package com.meteorshower.autoclock.view;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.bean.SpinnerOption;
import com.meteorshower.autoclock.http.SocketClient;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;
import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.util.ShellUtils;
import com.meteorshower.autoclock.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 发布shell命令页面
 */
public class CommandExecuteActivity extends BaseActivity {

    @BindView(R.id.et_command)
    EditText etCommand;
    @BindView(R.id.et_command_times)
    EditText etCommandTimes;
    @BindView(R.id.et_command_interval)
    EditText etCommandInterval;
    @BindView(R.id.tv_exc_result)
    TextView tvExcResult;
    @BindView(R.id.sp_command)
    Spinner spCommand;
    private String[] commands = new String[]{"进程存活查询", "进程停止", "点击钉钉", "工作台点击",
            "考勤打卡点击", "上班点击", "消息点击", "下班点击", "点击AC","启动进程","启动滑动"};

    @Override
    protected int getLayoutID() {
        return R.layout.activity_command_excute;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initData() {
        List<SpinnerOption> commandList = new ArrayList<>();
        for (int i = 0; i < commands.length; i++) {
            commandList.add(new SpinnerOption(String.valueOf(i + 1), commands[i]));
        }
        spCommand.setAdapter(new ArrayAdapter<SpinnerOption>(this, R.layout.support_simple_spinner_dropdown_item, commandList));
    }

    @OnClick({R.id.btn_exc_command})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_exc_command:
                tvExcResult.setText("");
                excCommands();
                break;
            default:
                break;
        }
    }

    private void excCommands() {
        try {
            String commands = etCommand.getText().toString();
            if (StringUtils.isEmptyOrNull(commands)) {
                commands = ((SpinnerOption) spCommand.getSelectedItem()).getValue();
            }
            if(commands.equals("10")){
                ShellUtils.CommandResult result = ShellUtils.execCommand(AccessibilityUtils.getCommand("12"),false);
                tvExcResult.setText(new Gson().toJson(result));
                return;
            }

            final int excTimes;
            final int excInterval;
            final String commandType = commands.trim();

            if (!StringUtils.isEmptyOrNull(etCommandTimes.getText().toString())) {
                excTimes = Integer.parseInt(etCommandTimes.getText().toString());
            } else {
                excTimes = 1;
            }

            if (!StringUtils.isEmptyOrNull(etCommandInterval.getText().toString())) {
                excInterval = Integer.parseInt(etCommandInterval.getText().toString());
            } else {
                excInterval = 1000;
            }

            int times = excTimes;
            while (times != 0) {
                String command = AccessibilityUtils.getCommand(commandType);
                Log.d("command","command="+command);
                excCommand(command);
                times--;
                Thread.sleep(excInterval);
            }
        } catch (Exception e) {
            tvExcResult.setText(Log.getStackTraceString(e));
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


}
