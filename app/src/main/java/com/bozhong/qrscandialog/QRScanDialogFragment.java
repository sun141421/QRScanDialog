package com.bozhong.qrscandialog;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.PermissionChecker;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

/**
 * 二维码扫描对话框
 * 使用{@link #show(FragmentManager, OnQRCodeReaded)}启动对话框
 * Created by lsc on 2017/9/9.
 */

@SuppressWarnings("unused")
public class QRScanDialogFragment extends DialogFragment {
    private static final int REQUEST_CODE_CAMERA=2017;

    private FrameLayout rootView;

    private QRCodeReaderView qrCodeReaderView;

    private OnQRCodeReaded callback;

    public static void show(@NonNull FragmentManager manage, @Nullable OnQRCodeReaded callback) {
        QRScanDialogFragment qrScanDialogFragment = new QRScanDialogFragment();
        qrScanDialogFragment.setCallback(callback);
        qrScanDialogFragment.show(manage, "QRScanDialogFragment");
    }

    private void setCallback(OnQRCodeReaded callback) {
        this.callback = callback;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_FRAME, 0);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //实现全屏
//        <!--关键点1-->
        //noinspection ConstantConditions
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
//        <!--关键点2-->
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0x00000000));
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = new FrameLayout(inflater.getContext());
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!hasCameraPermissionGranted()) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        } else {
            setupQRCodeReaderView();
        }
    }

    /**
     * 是否有相机权限
     */
    private boolean hasCameraPermissionGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_CAMERA && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            setupQRCodeReaderView();
        } else {
            setupNoPermissionView();
        }
    }

    private void setupNoPermissionView() {
        TextView tvInfo = new TextView(getContext());
        tvInfo.setBackgroundColor(Color.WHITE);
        tvInfo.setGravity(Gravity.CENTER);
        tvInfo.setText("扫描二维码需要有相机权限！\n点击去设置界面开启权限！");
        tvInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAppDetailSettings(getContext());
            }
        });
        rootView.addView(tvInfo);
    }

    /**
     * 打开本应用的详情页
     */
    private static void openAppDetailSettings(Context context) {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(i);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        } else if (hasCameraPermissionGranted()) {
            setupQRCodeReaderView();
        }
    }

    private void setupQRCodeReaderView() {
        qrCodeReaderView = new QRCodeReaderView(getContext());
        rootView.addView(qrCodeReaderView);
        qrCodeReaderView.setOnQRCodeReadListener(new QRCodeReaderView.OnQRCodeReadListener() {
            @Override
            public void onQRCodeRead(String text, PointF[] points) {
                if (callback != null) {
                    callback.onQRCodeReaded(QRScanDialogFragment.this, text);
                }
            }
        });
        qrCodeReaderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                qrCodeReaderView.forceAutoFocus();
            }
        });
        qrCodeReaderView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }

    }

    @SuppressWarnings("WeakerAccess")
    public interface OnQRCodeReaded {
        /**扫描得到文本后的callback*/
        void onQRCodeReaded(QRScanDialogFragment dialog, String readedText);
    }
}
