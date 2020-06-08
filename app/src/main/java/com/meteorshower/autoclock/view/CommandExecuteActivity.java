package com.meteorshower.autoclock.view;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.meteorshower.autoclock.R;
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
                String command = AccessibilityUtils.getCommand(etCommand.getText().toString().trim());
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


}
