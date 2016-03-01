package com.baiiu.krtvdemo.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 模拟数据工具类，数据文件位于assets目录下
 *
 * @author baiiu
 */
public class AssetsUtil {

    public static String readAssets(Context context, String name) {
        try {
            InputStream is = context.getAssets().open(name);
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line.trim());
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
