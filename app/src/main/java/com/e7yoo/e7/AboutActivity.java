package com.e7yoo.e7;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.e7yoo.e7.util.ActivityUtil;
import com.e7yoo.e7.util.OsUtil;
import com.e7yoo.e7.util.ShareDialogUtil;
import com.sdsmdg.tastytoast.TastyToast;

import cn.jiguang.share.android.api.JShareInterface;

public class AboutActivity extends BaseActivity implements OnClickListener {

    @Override
    protected String initTitle() {
        return null;
    }

    @Override
    protected int initLayoutResId() {
        return R.layout.activity_about;
    }

    @Override
    protected void initView() {

    }

    @Override
    protected void initSettings() {
        ((TextView) findViewById(R.id.tv_app_version)).setText(OsUtil.getAppVersionName(this));
    }

    @Override
    protected void initViewListener() {
        findViewById(R.id.actionbar_back).setOnClickListener(this);
        findViewById(R.id.iv_evaluate).setOnClickListener(this);
        findViewById(R.id.iv_share).setOnClickListener(this);
        findViewById(R.id.ll_qr_code).setOnClickListener(this);
        findViewById(R.id.ll_qq_group).setOnClickListener(this);
        findViewById(R.id.ll_wx).setOnClickListener(this);
        findViewById(R.id.ll_app_version).setOnClickListener(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.actionbar_back:
                onBackPressed();
                break;
            case R.id.iv_evaluate:
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("market://details?id=" + this.getPackageName()));
                    startActivity(intent);
                } catch (Exception e) {
                    TastyToast.makeText(this, getString(R.string.about_evaluate_error), TastyToast.LENGTH_SHORT,
                            TastyToast.WARNING);
                }
                break;
            case R.id.iv_share:
                ShareDialogUtil.show(this);
                break;
            case R.id.ll_qr_code:
                qrCodeDialog();
                break;
            case R.id.ll_qq_group:
                if (!joinQQGroup("5JXaNRVlRDs3IrCcpkVh40fu-wrz6NCn")) {
                    final android.text.ClipboardManager clipboard =
                            (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(getString(R.string.about_qq_group_num));
                    TastyToast.makeText(this, getString(R.string.about_qq_group_num_clip), TastyToast.LENGTH_SHORT, TastyToast.INFO);
                }
                break;
            case R.id.ll_wx:
                final android.text.ClipboardManager clipboard2 =
                        (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard2.setText(getString(R.string.about_wx_num));
                TastyToast.makeText(this, getString(R.string.about_qq_group_num_clip), TastyToast.LENGTH_SHORT, TastyToast.INFO);
                break;
            case R.id.ll_app_version:
                // UpdateHelper.getInstance().manualUpdate(getApplicationContext().getPackageName());
                break;
            default:
                break;
        }
    }

    /****************
     *
     * 发起添加群流程。群号：小萌伴--反馈群(364328556) 的 key 为： 5JXaNRVlRDs3IrCcpkVh40fu-wrz6NCn
     * 调用 joinQQGroup(5JXaNRVlRDs3IrCcpkVh40fu-wrz6NCn) 即可发起手Q客户端申请加群 小萌伴--反馈群(364328556)
     *
     * @param key 由官网生成的key
     * @return 返回true表示呼起手Q成功，返回fals表示呼起失败
     ******************/
    public boolean joinQQGroup(String key) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("mqqopensdkapi://bizAgent/qm/qr?url=http%3A%2F%2Fqm.qq.com%2Fcgi-bin%2Fqm%2Fqr%3Ffrom%3Dapp%26p%3Dandroid%26k%3D" + key));
        // 此Flag可根据具体产品需要自定义，如设置，则在加群界面按返回，返回手Q主界面，不设置，按返回会返回到呼起产品界面    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            startActivity(intent);
            return true;
        } catch (Exception e) {
            // 未安装手Q或安装的版本不支持
            return false;
        }
    }

    private QrcodeDialogUtil qrCodeUtil;

    /**
     * mydialogutil
     */
    private void qrCodeDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (qrCodeUtil == null) {
                    qrCodeUtil = new QrcodeDialogUtil(AboutActivity.this, R.style.qrcode_dialog);
                }
                qrCodeUtil.show();
            }
        });
    }

    /**
     * 自定义Dialog
     *
     * @author goome
     * @category 显示二维码使用
     */
    public class QrcodeDialogUtil extends Dialog {

        public QrcodeDialogUtil(Context context, int theme) {
            super(context, theme);
            this.setCanceledOnTouchOutside(true);
        }

        public QrcodeDialogUtil(Context context) {
            super(context);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_qr_code);
            try {
                getWindow().getDecorView().findViewById(R.id.qrcode).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // ShareUtil.share(AboutActivity.this, getString(R.string.qr_code_share_title), getString(R.string.qr_code_share_content), null, new UMImage(AboutActivity.this, R.drawable.e7_qrcode_360));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this**/
        // UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        Log.d("result", "onActivityResult");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareDialogUtil.release();
    }
}
