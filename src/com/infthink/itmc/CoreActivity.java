package com.infthink.itmc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.infthink.itmc.base.BaseActivity;
import com.infthink.itmc.service.CoreService;
import com.infthink.libs.common.utils.NetworkUtils;

public class CoreActivity extends BaseActivity<CoreService> {

    public static final int REQUEST_CODE_NETWORK_SETTINGS = 100;
    private static boolean sDoNegativieClicked;

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
    }

    private void checkNetworkStatus() {
        if (!NetworkUtils.isNetWorkConnected(getContext())) {
            // DialogFragment
            NetworkDialogFragment networkDialog = NetworkDialogFragment.newInstance("网络链接不可用");
            networkDialog.show(getFragmentManager(), "networkDialog");
        }
    }

    protected void doPositiveClickOnNetworkDialog() {
        startActivityForResult(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS), REQUEST_CODE_NETWORK_SETTINGS);
    }

    protected void doNegativieClickOnNetworkDialog() {
        // finish();
        sDoNegativieClicked = true;
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
            return new AlertDialog.Builder(getActivity()).setIcon(android.R.drawable.ic_dialog_alert).setTitle(title).setCancelable(false)
                    .setPositiveButton("去链接网络", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((CoreActivity) getActivity()).doPositiveClickOnNetworkDialog();
                        }
                    }).setNegativeButton("暂不设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ((CoreActivity) getActivity()).doNegativieClickOnNetworkDialog();
                        }
                    }).create();
        }

    }

}
