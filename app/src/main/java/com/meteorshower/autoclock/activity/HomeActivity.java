package com.meteorshower.autoclock.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.Job.TestClickJob;
import com.meteorshower.autoclock.JobThread.JobExecutor;
import com.meteorshower.autoclock.JobThread.JobFactory;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.service.ControllerAccessibilityService;
import com.meteorshower.autoclock.util.AccessibilityUtils;
import com.meteorshower.autoclock.util.DialogUtils;
import com.meteorshower.autoclock.util.IOUtils;
import com.meteorshower.autoclock.util.LogUtils;

import java.io.File;
import java.util.List;

import butterknife.OnClick;

public class HomeActivity extends BaseActivity {

    private String[] permissions = new String[]{
            Permission.ACCESS_FINE_LOCATION,
            Permission.ACCESS_COARSE_LOCATION,
            Permission.MANAGE_EXTERNAL_STORAGE,
            Permission.READ_PHONE_STATE,
            Permission.CAMERA,
            Permission.RECORD_AUDIO
    };
    private boolean normalPermission = false, installPermission = false, alertPermission = false;//普通权限，安装权限，悬浮窗权限

    @Override
    protected int getLayoutID() {
        return R.layout.activity_home;
    }

    @Override
    protected void initView() {
        if (ControllerAccessibilityService.getInstance() == null) {
            Toaster.show("无障碍服务未启动");
            return;
        }
    }

    @Override
    protected void initData() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        if (dm.heightPixels > dm.widthPixels) {
            AppConstant.ScreenHeight = dm.heightPixels;
            AppConstant.ScreenWidth = dm.widthPixels;
        } else {
            AppConstant.ScreenHeight = dm.widthPixels;
            AppConstant.ScreenWidth = dm.heightPixels;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }

    @OnClick({R.id.btn_start, R.id.btn_end, R.id.btn_add, R.id.btn_check, R.id.btn_look,
            R.id.btn_test, R.id.btn_reset, R.id.btn_exc_cmd, R.id.btn_setting, R.id.btn_scroll_setting})
    public void doClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start:
                if (!JobExecutor.getInstance().isRunning()) {
                    JobExecutor.getInstance().setRunning(true);
                    JobExecutor.getInstance().start();
                }
                if (!JobFactory.getInstance().isRunning()) {
                    JobFactory.getInstance().setRunning(true);
                    JobFactory.getInstance().start();
                }
                JobFactory.getInstance().setGetJob(true);
                break;
            case R.id.btn_end:
                JobFactory.getInstance().setGetJob(false);
                break;
            case R.id.btn_check:
                JobFactory.getInstance().setRunning(false);
                JobExecutor.getInstance().setRunning(false);
                break;
            case R.id.btn_look:
                startActivity(new Intent(HomeActivity.this, CheckJobActivity.class));
                break;
            case R.id.btn_add:
                startActivity(new Intent(HomeActivity.this, AddJobActivity.class));
                break;
            case R.id.btn_test:
                startActivity(new Intent(HomeActivity.this, CheckHeartActivity.class));
                break;
            case R.id.btn_reset:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        new TestClickJob(null).doJob();
                    }
                }).start();
                break;
            case R.id.btn_exc_cmd:
                startActivity(new Intent(HomeActivity.this, CommandExecuteActivity.class));
                break;
            case R.id.btn_setting:
                AccessibilityUtils.goToAccessibilitySetting(HomeActivity.this);
                break;
            case R.id.btn_scroll_setting:
                startActivity(new Intent(HomeActivity.this, ScrollSettingActivity.class));
                break;
            default:
                break;
        }
    }

    /**
     * 检查权限
     */
    private void checkPermissions() {
        if (!XXPermissions.isGrantedPermission(HomeActivity.this, permissions)) {
            showPermissionNoticeDialog(permissions);
        } else {
            normalPermission = true;
        }

        if (normalPermission) {
            try {
                getSpecialPermission();
            } catch (Exception e) {
                showExceptionNotice(e);
            }
        }
    }

    /**
     * 提示申请权限
     */
    private void showPermissionNoticeDialog(String[] permissions) {
        DialogUtils.showAlert(HomeActivity.this, "权限提醒", "本应用需要申请以下权限才能使用，请允许",
                "确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        applyPermission(permissions);
                    }
                }, "退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }

    /**
     * 申请权限
     */
    private void applyPermission(String[] permissions) {
        XXPermissions.with(HomeActivity.this)
                .permission(permissions)
                .request(new OnPermissionCallback() {
                             @Override
                             public void onGranted(List<String> permissions, boolean all) {
                                 if (permissions.size() > 0) {
                                     if (all) {
                                         normalPermission = true;
                                         try {
                                             getSpecialPermission();
                                         } catch (Exception e) {
                                             showExceptionNotice(e);
                                         }
                                     }
                                 }
                             }

                             @Override
                             public void onDenied(List<String> permissions, boolean never) {
                                 String deniedStr = getDeniedPermissionsName(permissions);
                                 showSystemPermissionSettingDialog(permissions, deniedStr);
                             }
                         }

                );
    }


    /**
     * 获取特殊权限
     */
    private void getSpecialPermission() throws Exception {
        if (checkInstallPermission()) {
            installPermission = true;
        }
        if (checkAlertPermission()) {
            alertPermission = true;
        }
        initAllDirs();
    }

    /**
     * 8.0及以上
     * 检查安装未知来源应用权限
     */
    private boolean checkInstallPermission() {
        if (Build.VERSION.SDK_INT >= 26 && !getPackageManager().canRequestPackageInstalls()) {
            Toaster.show( "本应用需要获得安装未知应用权限，否则无法正常使用！");
            Uri uri = Uri.parse("package:" + getPackageName());
            Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, uri);
            startActivity(intent);
            return false;
        }
        return true;
    }

    /**
     * 6.0及以上
     * 检查悬浮窗权限，直播需要这个权限
     */
    private boolean checkAlertPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(HomeActivity.this)) {
            Toaster.show( "本应用需要获得悬浮窗权限，否则无法正常使用！");
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
            startActivity(intent);
            return false;
        }
        return true;
    }

    /**
     * 去系统设置界面赋予权限
     */
    private void showSystemPermissionSettingDialog(List<String> deniedPermissions, String deniedStr) {
        DialogUtils.showAlert(HomeActivity.this, "权限提醒", "本应用需要赋予已禁止的" + deniedStr + "权限才能使用，需要到系统设置界面赋予权限",
                "去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XXPermissions.startPermissionActivity(HomeActivity.this, deniedPermissions);
                    }
                }, "退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
    }

    private String getDeniedPermissionsName(List<String> denied) {
        if (denied == null || denied.size() <= 0) {
            return "";
        }
        String result = "";
        for (String per : denied) {
            if (!result.contains("位置") && (per.equalsIgnoreCase(Permission.ACCESS_FINE_LOCATION) || per.equalsIgnoreCase(Permission.ACCESS_COARSE_LOCATION))) {
                result += "/位置";
            } else if (!result.contains("存储") && (per.equalsIgnoreCase(Permission.READ_EXTERNAL_STORAGE) || per.equalsIgnoreCase(Permission.WRITE_EXTERNAL_STORAGE))) {
                result += "/存储";
            } else if (per.equalsIgnoreCase(Permission.READ_PHONE_STATE)) {
                result += "/电话";
            } else if (per.equalsIgnoreCase(Permission.CAMERA)) {
                result += "/相机";
            }
        }
        if (result.contains("/")) {
            result = result.substring(result.indexOf("/") + 1, result.length());
        }
        return result;
    }

    /**
     * 数据库版本降级提醒
     */
    private void showExceptionNotice(Exception e) {
        String message = e.getMessage();
        if (message.contains("Can't downgrade database")) {
            DialogUtils.showPrompt(HomeActivity.this, "异常提示", "数据库版本过低，请联系管理员", "退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
        } else {
            DialogUtils.showPrompt(HomeActivity.this, "异常提示", "系统更新异常，请联系管理员", "退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
        }
    }

    private void initAllDirs() {
        String root = IOUtils.getRootStoragePath(HomeActivity.this);
        for (String dir : AppConstant.DIRECTORYS) {
            File d = new File(root + dir);
            if (!d.exists()) {
                d.mkdirs();
            }
        }
    }

}
