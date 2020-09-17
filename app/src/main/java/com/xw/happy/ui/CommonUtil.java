package com.xw.happy.ui;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

import com.xw.happy.activity.MainActivity;
import com.xw.happy.happyApplication;
import com.xw.helper.utils.MLog;

public class CommonUtil {

    private static final String CONTENT_URI_QUERY = "content://com.hunantv.operator.mango.sp.provider/spaction";


    public interface ActionColumns extends BaseColumns {
        String NAME = "name";
        String VALUE = "value";
    }
    /**
     * 用于查询sp跳转能力
     *
     * @return
     */
    public static int getAbility(String pageSource) {
        MLog.d("getAbility in info:" + pageSource);
        Uri uri = Uri.parse(CONTENT_URI_QUERY);
        Cursor mCursor = happyApplication.getInstance().getContentResolver().query(uri, null,
                null, null, null);
        int result = 0;
        if (mCursor != null) {
            while (mCursor.moveToNext()) {
                String name = mCursor.getString(mCursor.getColumnIndex(ActionColumns.NAME));
                if (pageSource.equals(name)) {
                    result = mCursor.getInt(mCursor.getColumnIndex(ActionColumns.VALUE));
                    MLog.d( "query:" + result);
                }
            }
            MLog.d("getAbility result:" + result);
            mCursor.close();
        }
        return result;
    }
}
