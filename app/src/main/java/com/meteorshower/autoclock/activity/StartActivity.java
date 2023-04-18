package com.meteorshower.autoclock.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentActivity;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.Toaster;
import com.meteorshower.autoclock.R;
import com.meteorshower.autoclock.constant.AppConstant;
import com.meteorshower.autoclock.util.DialogUtils;
import com.meteorshower.autoclock.util.IOUtils;

import java.io.File;
import java.util.List;


/**
 * 启动页
 */
public class StartActivity extends FragmentActivity {

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        setContentView(R.layout.activity_start);
    }


    @Override
    protected void onStart() {
        super.onStart();
        checkPermissions();
    }


    /**
     * 检查权限
     */
    private void checkPermissions() {
        if (!XXPermissions.isGrantedPermission(StartActivity.this, permissions)) {
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
        DialogUtils.showAlert(StartActivity.this, "权限提醒", "本应用需要申请以下权限才能使用，请允许",
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
        XXPermissions.setDebugMode(true);
        XXPermissions.with(StartActivity.this)
                .permission(permissions)
                .request(new OnPermissionCallback() {
                             @Override
                             public void onGranted(List<String> permissions, boolean all) {
                                 Log.d(AppConstant.TAG, "onGranted permissions=" + permissions);
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
                                 Log.d(AppConstant.TAG, "onGranted permissions=" + permissions);
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
            Toaster.show("本应用需要获得安装未知应用权限，否则无法正常使用！");
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
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(StartActivity.this)) {
            Toaster.show("本应用需要获得悬浮窗权限，否则无法正常使用！");
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
        DialogUtils.showAlert(StartActivity.this, "权限提醒", "本应用需要赋予已禁止的" + deniedStr + "权限才能使用，需要到系统设置界面赋予权限",
                "去设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        XXPermissions.startPermissionActivity(StartActivity.this, deniedPermissions);
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
            DialogUtils.showPrompt(StartActivity.this, "异常提示", "数据库版本过低，请联系管理员", "退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
        } else {
            DialogUtils.showPrompt(StartActivity.this, "异常提示", "系统更新异常，请联系管理员", "退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    System.exit(0);
                }
            });
        }
    }

    private void initAllDirs() {
        String root = IOUtils.getRootStoragePath(StartActivity.this);
        for (String dir : AppConstant.DIRECTORYS) {
            File d = new File(root + dir);
            if (!d.exists()) {
                d.mkdirs();
            }
        }
        jumpToHomePage();
    }

    private void jumpToHomePage(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(StartActivity.this, HomeActivity.class));
                        StartActivity.this.finish();
                    }
                });
            }
        },3 * 1000);
    }

}