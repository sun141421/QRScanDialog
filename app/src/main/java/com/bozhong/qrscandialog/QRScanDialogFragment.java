package com.bozhong.qrscandialog;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;

/**
 * 二维码扫描对话框
 * Created by lsc on 2017/9/9.
 */

public class QRScanDialogFragment extends DialogFragment {

    private QRCodeReaderView qrCodeReaderView;

    private QRCodeReaderView.OnQRCodeReadListener callback;

    public static void show(@NonNull FragmentManager manage, @Nullable QRCodeReaderView.OnQRCodeReadListener callback) {
        QRScanDialogFragment qrScanDialogFragment = new QRScanDialogFragment();
        qrScanDialogFragment.setCallback(callback);
        qrScanDialogFragment.show(manage, "QRScanDialogFragment");
    }

    private void setCallback(QRCodeReaderView.OnQRCodeReadListener callback) {
        this.callback = callback;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        qrCodeReaderView = new QRCodeReaderView(inflater.getContext());
        setupCallback();
        return qrCodeReaderView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (!hasCameraPermissionGranted()) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 2017);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 2017 && grantResults[0] == PermissionChecker.PERMISSION_GRANTED) {
            qrCodeReaderView.startCamera();
        }else{
            Toast.makeText(getContext(), "扫描二维码需要有相机权限！请在应用设置界面开启权限！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 是否有相机权限
     */
    private boolean hasCameraPermissionGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || PermissionChecker.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED;
    }


    private void setupCallback() {
        qrCodeReaderView.setOnQRCodeReadListener(callback);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (hasCameraPermissionGranted()) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (hasCameraPermissionGranted()) {
            qrCodeReaderView.stopCamera();
        }

    }
}
