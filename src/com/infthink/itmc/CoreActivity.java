package com.infthink.itmc;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.infthink.itmc.adapter.CastDeviceAdapter;
import com.infthink.itmc.base.BaseActivity;
import com.infthink.itmc.data.NetcastManager.CastSearchListener;
import com.infthink.itmc.data.NetcastManager.CastSessionListener;
import com.infthink.itmc.service.CoreService;
import com.infthink.libs.common.utils.NetworkUtils;
import com.infthink.netcast.sdk.CastDevice;

public class CoreActivity extends BaseActivity<CoreService> implements CastSearchListener, CastSessionListener {

    public static final int REQUEST_CODE_NETWORK_SETTINGS = 100;
    private static boolean sDoNegativieClicked;
    
    private AlertDialog mAvailableDevices = null;
    private AlertDialog mConnectedDevices = null;
    private ListView mListView = null;
    private ProgressDialog mProgressDialog = null;
    private CastDeviceAdapter mCastDeviceAdapter;
    private ImageView mCastView;

    @Override
    protected final Class<CoreService> getServiceClass() {
        return CoreService.class;
    }

    @Override
    protected void onCreateAfterSuper(Bundle savedInstanceState) {
        super.onCreateAfterSuper(savedInstanceState);
        if (!sDoNegativieClicked) {
            checkNetworkStatus();
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Connecting to cast device...");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        mListView = (ListView) (LayoutInflater.from(this).inflate(
                R.layout.devices_list, null).findViewById(R.id.lvdeviceslist));
        mAvailableDevices = new AlertDialog.Builder(this)
                .setIcon(R.drawable.img_cast_normal).setTitle("Connect to")
                .setView(mListView).create();
        mAvailableDevices.setCancelable(true);
        
        mCastDeviceAdapter = new CastDeviceAdapter(this);
        mListView.setAdapter(mCastDeviceAdapter);
        ITApp.getNetcastManager().setCastSearchListener(this);
        ITApp.getNetcastManager().setCastSessionListener(this);
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                if (ITApp.getNetcastManager().connectDevice((CastDevice) mListView.getItemAtPosition(position))) {
                    mAvailableDevices.dismiss();
                    mProgressDialog.show();
                }
            }
        });
        createCastView();
    }

    private void checkNetworkStatus() {
        if (!NetworkUtils.isNetWorkConnected(getContext())) {
            // DialogFragment
            NetworkDialogFragment networkDialog = NetworkDialogFragment
                    .newInstance("网络链接不可用");
            networkDialog.show(getFragmentManager(), "networkDialog");
        }
    }

    protected void doPositiveClickOnNetworkDialog() {
        startActivityForResult(new Intent(
                android.provider.Settings.ACTION_WIFI_SETTINGS),
                REQUEST_CODE_NETWORK_SETTINGS);
    }

    protected void doNegativieClickOnNetworkDialog() {
        // finish();
        sDoNegativieClicked = true;
    }

    protected void showCastList() {
        if (isSessionEstablished()) {
            if (ITApp.getNetcastManager().isConnectedDevice()) {
                if (mConnectedDevices == null) {
                    mConnectedDevices = new AlertDialog.Builder(this)
                            .setIcon(R.drawable.img_cast_pressed)
                            .setTitle("Connected to")
                            .setMessage(ITApp.getNetcastManager().getCurrentDeviceName())
                            .setNegativeButton("Disconnect",
                                    new DialogInterface.OnClickListener() {

                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            ITApp.getNetcastManager()
                                                    .disconnectDevice();
                                        }
                                    }).create();
                    mConnectedDevices.setCancelable(true);
                }
                mConnectedDevices.show();
            } else {
                
            }
        } else {
            mAvailableDevices.show();
            ITApp.getNetcastManager().startSearch();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
        case REQUEST_CODE_NETWORK_SETTINGS:
            checkNetworkStatus();
            break;

        default:
            break;
        }
    }

    public static class NetworkDialogFragment extends DialogFragment {
        public static NetworkDialogFragment newInstance(String title) {
            NetworkDialogFragment instance = new NetworkDialogFragment();
            Bundle args = new Bundle();
            args.putString("title", title);
            instance.setArguments(args);
            return instance;
        }

        @Override
        public Dialog onCreateDialog(Bundle saveInstanceState) {

            String title = getArguments().getString("title");
            return new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(title)
                    .setCancelable(false)
                    .setPositiveButton("去链接网络",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    ((CoreActivity) getActivity())
                                            .doPositiveClickOnNetworkDialog();
                                }
                            })
                    .setNegativeButton("暂不设置",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    ((CoreActivity) getActivity())
                                            .doNegativieClickOnNetworkDialog();
                                }
                            }).create();
        }
    }

    @Override
    public void onSearchFinish() {
    }

    @Override
    public void onCastDevicesUpdate() {
        mCastDeviceAdapter.setDeviceList((ArrayList<CastDevice>) ITApp.getNetcastManager().getCastDeviceList());
    }

    @Override
    public void onSearchStart() {
        
    }

    @Override
    public void onSessionStarted() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        updateCastBtnState();
    }

    @Override
    public void onSessionFailed() {
        mProgressDialog.dismiss();
        updateCastBtnState();
    }

    @Override
    public void onSessionEnded() {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        updateCastBtnState();
    }
    
    protected void updateCastBtnState() {
        if (mCastView == null) return;
        if (isSessionEstablished()) {
            mCastView.setImageResource(R.drawable.img_cast_pressed);
        } else {
            mCastView.setImageResource(R.drawable.img_cast_normal);
        }
    }
    
    protected boolean isSessionEstablished() {
        return ITApp.getNetcastManager().isSessionEstablished();
    }
    
    protected void createCastView() {
        mCastView = new ImageView(this);
        mCastView.setImageResource(R.drawable.img_cast_normal);
        mCastView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showCastList();
            }
        });
    }
    
    protected ImageView getCastView() {
        return mCastView;
    }
}
