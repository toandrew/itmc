package com.infthink.itmc.util;

import android.content.Context;
import android.widget.Toast;

public class AlertMessage {
    private static Toast curToast = null;

    public static void show(Context paramContext, int paramInt) {
        show(paramContext, paramInt, false);
    }

    public static void show(Context paramContext, int paramInt, boolean paramBoolean) {
        show(paramContext, paramContext.getString(paramInt), paramBoolean);
    }

    public static void show(Context paramContext, String paramString) {
        show(paramContext, paramString, false);
    }

    public static void show(Context paramContext, String paramString, boolean paramBoolean) {
        int i = 1;
        if (curToast != null) curToast.cancel();
        while (true) {
            try {
                if (Util.isEmpty(paramString)) {
                    curToast = Toast.makeText(paramContext, paramString, i);
                    curToast.show();
                    return;
                }
            } catch (Exception localException) {
                return;
            }
            do {
                i = 0;
                break;
            } while (!paramBoolean);

        }
    }
}
