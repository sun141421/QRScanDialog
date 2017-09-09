# QRScanDialog

QRScanDialog是一个简单的二维码扫描对话框，处理了动态权限。


[![Release](https://jitpack.io/v/sun141421/QRScanDialog.svg)](https://jitpack.io/#sun141421/QRScanDialog)

# 集成方法

工程目录下的build.gradle文件添加
```
repositories {
        jcenter()
        maven { url "https://jitpack.io" }
   }
  ```
   在app模块目录下的build.gradle文件添加
   ```
   dependencies {
         compile 'com.github.sun141421:QRScanDialog:{latest version}'
   }
   ```

# 使用方法
   ```
 QRScanDialogFragment.show(getSupportFragmentManager(), new QRScanDialogFragment.OnQRCodeReaded() {
        @Override
        public void onQRCodeReaded(QRScanDialogFragment dialog,String readedTxt) {
           Toast.makeText(MainActivity.this, "二维码文字："+readedTxt, Toast.LENGTH_SHORT).show();
           dialog.dismiss();
        }
    );
   ```

## 内部使用的二维码扫描库

### ![QRCodeReaderView](https://github.com/dlazaro66/QRCodeReaderView)