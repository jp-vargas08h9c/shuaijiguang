package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;

import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;


import com.example.shuaijiguang.android_tiku_demo.R;
import com.google.gson.JsonParseException;

@SuppressWarnings("deprecation")
public class SystemHelper {

	private static final String TAG = "SystemHelper";

	private SystemHelper() {
	}

	public static void makeExceptionToast(Context ctx, int code, Throwable tr) {
		StringBuilder sb = new StringBuilder();
		if(code != -100){
			sb.append("(").append(code).append(") ");
		}
		
		if (tr instanceof ConnectTimeoutException) {
			sb.append(ctx.getResources().getString(R.string.network_not_connected));
		} else if(tr instanceof NetworkErrorException) {
			sb.append(ctx.getResources().getString(R.string.network_not_connected));
		} else if(tr instanceof SocketTimeoutException) {
			sb.append(ctx.getResources().getString(R.string.socket_exception));
		} else if(tr instanceof FileNotFoundException) {
			sb.append(ctx.getResources().getString(R.string.file_notfound_exception));
		} else if(tr instanceof IOException) {
			sb.append(ctx.getResources().getString(R.string.io_exception));
		} else if(tr instanceof JsonParseException) {
			sb.append(ctx.getResources().getString(R.string.data_parser_exception));
		} else if(tr instanceof UnsupportedEncodingException){
			sb.append(ctx.getResources().getString(R.string.data_parser_exception));
		}else {
			sb.append(ctx.getResources().getString(R.string.unknown_exception));
		}
		
		Toast.makeText(ctx, sb.toString(), Toast.LENGTH_SHORT).show();
	}
	
	public static void makeExceptionToast(Context ctx,Throwable tr) {
		makeExceptionToast(ctx, -100, tr);
	}
	
	/**
	 * ?????????????????????????????????<br/>
	 * ???????????????????????????android.permission.ACCESS_NETWORK_STATE
	 * 
	 * @param context
	 * @return ??????0??????GPRS??????; ??????1,??????WIFI??????; ??????-1?????????????????????
	 */
	public static int getNetWorkType(Context context) {
		int code = -1;
		try{
			ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(null != connManager) {
				State state = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
				if(State.CONNECTED == state) {
					code = ConnectivityManager.TYPE_WIFI;
				} else {
					state = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
					if(State.CONNECTED == state) {
						code = ConnectivityManager.TYPE_MOBILE;
					}
				}
			}
		} catch(Exception e) {
			LogHelper.d(TAG, "Exception:" + e);
		}
		return code;
	}
	
	/**
	 * ???????????????????????????????????????<br/>
	 * ???????????????android.os.Build??????????????????????????????????????????
	 * 
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getScreenInfo(Context context) {
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		DisplayMetrics dm = new DisplayMetrics();
		windowManager.getDefaultDisplay().getMetrics(dm);
		
		//int w = dm.widthPixels;//??????????????????
		//int h = dm.heightPixels; //??????????????????
		//float d = dm.density; //?????????0.75 / 1.0 / 1.5???
		//int densityDpi = dm.densityDpi;  // ????????????DPI(160 / 240 / 320 / 480)

		return dm;
	}
	
	/**
	 * ??????????????????,???0.75/1.0/1.5
	 * 
	 * @return ??????float???
	 */
	public static float getDensity(Context context) {
		DisplayMetrics dm = getScreenInfo(context);
		return dm.density;
	}
	
	/**
	 * ??????????????????,???160/240/320/480
	 * 
	 * @return ??????dpi???
	 */
	public static int getDensityDpi(Context context) {
		DisplayMetrics dm = getScreenInfo(context);
		return dm.densityDpi;
	}
	
	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	public static int getWidthPixels(Context context) {
		DisplayMetrics dm = getScreenInfo(context);
		return dm.widthPixels;
	}
	
	/**
	 * ???????????????????????????
	 * 
	 * @return
	 */
	public static int getHeightPixels(Context context) {
		DisplayMetrics dm = getScreenInfo(context);
		return dm.heightPixels;
	}
	
	/**
	 * ???????????????Dpi
	 * @param context
	 * @param px ?????????
	 * @return ??????Dpi???
	 */
	public static int px2dip(Context context, float px){
	    return (int)(0.5F + px / getDensity(context));
	}
	
	/**
	 * ??????Dpi?????????
	 * @param context
	 * @param dip ??????Dpi???
	 * @return ?????????
	 */
	public static int dip2px(Context context, float dip){
	    return (int)(0.5F + dip * getDensity(context));
	}
	
	/**
	 * ???px????????????sp???
	 * 
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2sp(Context context, float pxValue) {  
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;  
		return (int) (pxValue / fontScale + 0.5f);  
	}  

	/**
	 * ???sp????????????px???
	 * 
	 * @param context
	 * @param spValue
	 * @return
	 */
	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}


	/**
	 * ????????????????????????
	 * @param context
	 * @return
	 */

	public static int getStatusBarHight(Context context){
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = context.getResources().getDimensionPixelSize(x);
			return sbar;
		} catch (Exception e1) {
			e1.printStackTrace();
			return -1;
		}
	}


	/**
	 * ???????????????<br/>
	 * ???????????????????????????android.permission.READ_PHONE_STATE?????????,??????????????????????????????????????????????????????
	 * 
	 * @param context
	 * @return
	 */

	public static String getMobileNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getLine1Number();
	}
	
	/**
	 * ??????SIM???????????????<br/>
	 * ???????????????????????????android.permission.READ_PHONE_STATE?????????,???????????????????????????????????????????????????????????????
	 * 
	 * @param context
	 * @return
	 */
	public static String getSimSerialNumber(Context context) {
		TelephonyManager tm = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tm.getSimSerialNumber();
	}
	
	/**
	 * ??????????????????????????????,???:1<br/>
	 * ??????????????????AndroidMainfest.xml???manifest?????????android:versionCode?????????
	 * @param context
	 * @return ????????????????????????
	 */
	public static int getAppVersionCode(Context context) {
		int versionCode = -1;
		try {
			PackageManager pm = context.getPackageManager();
			
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionCode = pi.versionCode;

		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
		return versionCode;
	}

	/**
	 * ???????????????????????????,???:1.0<br/>
	 * ??????????????????AndroidMainfest.xml???manifest?????????android:versionName?????????
	 * @param context
	 * @return ?????????????????????
	 */
	public static String getAppVersionName(Context context) {
		String versionName = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			versionName = pi.versionName;

		} catch (Exception e) {
			Log.e(TAG, "Exception", e);
		}
		return versionName;
	}

	/**
	 * ???????????????APK?????????????????????????????????????????????
	 * 
	 * @param context
	 * @param apkFilePath apk?????????????????????
	 */

	public static void installAPK(Context context, String apkFilePath) {

		File apkfile = new File(apkFilePath);

        if (apkfile.exists()) {

			Intent intent = new Intent(Intent.ACTION_VIEW);

			intent.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
			
			context.startActivity(intent);
        }
	}
}
