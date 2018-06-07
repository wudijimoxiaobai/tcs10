package com.csscaps.tcs.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.csscaps.common.base.BaseFragment;
import com.csscaps.common.utils.AppSP;
import com.csscaps.common.utils.RegexUtils;
import com.csscaps.common.utils.ToastUtil;
import com.csscaps.tcs.R;
import com.csscaps.tcs.ServerConstants;
import com.csscaps.tcs.service.SynchronizeService;
import com.tax.fcr.library.network.Api;
import com.tax.fcr.library.network.IPresenter;
import com.tax.fcr.library.network.RequestModel;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by tl on 2018/6/1.
 */

public class NetworkConfigurationFragment extends BaseFragment implements IPresenter {

    @BindView(R.id.server_address)
    EditText mServerAddress;
    @BindView(R.id.server_port)
    EditText mServerPort;
    @BindView(R.id.upload_address)
    EditText mUploadAddress;

    private String serverAddress;
    private String serverPort;
    private String uploadAddress;

    @Override
    protected int getLayoutResId() {
        return R.layout.network_config_fragment;
    }

    @Override
    protected void onInitPresenters() {}

    @Override
    public void initView(Bundle savedInstanceState) {
        mServerAddress.setText(AppSP.getString("serverAddress"));
        mServerPort.setText(AppSP.getString("serverPort"));
        mUploadAddress.setText(AppSP.getString("uploadAddress"));
    }


    @OnClick({R.id.back, R.id.test})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                getActivity().finish();
                break;
            case R.id.test:
                test();
                break;
        }
    }

    private void test() {
        serverAddress = mServerAddress.getText().toString().trim();
        serverPort = mServerPort.getText().toString().trim();
        uploadAddress = mUploadAddress.getText().toString().trim();
        if (TextUtils.isEmpty(serverAddress)) {
            ToastUtil.showShort(getString(R.string.hit38));
            return;
        }else{
            if(!RegexUtils.isIP(serverAddress)){
                ToastUtil.showShort(getString(R.string.hit43));
                return;
            }
        }

        if (TextUtils.isEmpty(serverPort)) {
            ToastUtil.showShort(getString(R.string.hit39));
            return;
        } else {
            int port = Integer.valueOf(serverPort);
            if (port<1||port>65535){
                ToastUtil.showShort(getString(R.string.hit42));
                return;
            }
        }

       /* if (TextUtils.isEmpty(uploadAddress)) {
            ToastUtil.showShort(getString(R.string.hit41));
            return;
        }*/

        String url = String.format(getString(R.string.url_format), serverAddress, serverPort);
        Api.setBaseUrl(url);
        RequestModel requestModel = new RequestModel();
        requestModel.setFuncid(ServerConstants.A000);
        Api.post(this, requestModel);
    }

    @Override
    public void onSuccess(String requestPath, String objectString) {
        ToastUtil.showLong(getString(R.string.hit40));
        AppSP.putString("serverAddress", serverAddress);
        AppSP.putString("serverPort", serverPort);
        AppSP.putString("uploadAddress", uploadAddress);
        mContext.startService(new Intent(mContext, SynchronizeService.class).putExtra("autoSyn", true));
    }

    @Override
    public void onFailure(String requestPath, String errorMes) {
        switch (errorMes) {
            case Api.ERR_NETWORK:
                ToastUtil.showShort(getString(R.string.hit3));
                break;
            case Api.FAIL_CONNECT:
                ToastUtil.showShort(getString(R.string.hit4));
                break;
        }
    }
}
