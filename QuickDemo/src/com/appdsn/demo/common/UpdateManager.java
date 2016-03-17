package com.appdsn.demo.common;

import okhttp3.Call;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import com.appdsn.qa.common.http.OkHttpUtils;
import com.appdsn.qa.common.http.callback.StringCallback;
import com.appdsn.qa.utils.AppUtils;
import com.appdsn.qa.utils.FileUtils;

public class UpdateManager {

	private Context mContext;
	private CompleteReceiver completeReceiver;
	private String apkUrl = "http://appupdate.dymfilm.com/android/com.dym.film_1.6.0_dym_signed.apk";
	private String checkUrl = "http://www.appdsn.com";
	private String folderName="abc";//下载目录,如果是多级目录，确保父目录已经创建
	private String fileName="";//下载的文件名
	public UpdateManager(Context context) {
		this.mContext = context;
		completeReceiver = new CompleteReceiver();
	}

	public void checkUpdate() {
		OkHttpUtils.getInstance().get(mContext, checkUrl, new StringCallback() {

			@Override
			public void onSuccess(String response) {
				// TODO Auto-generated method stub
				try {
					JSONObject jObject = new JSONObject(response);
//					if (jObject.has("vercode")) {
//						int newVercode = jObject.getInt("vercode");
//						if (newVercode > AppUtils.getVersionCode(mContext)) {
//							fileName="appname" + jObject.getString("vername") + ".apk";
//							showAlertDialog(jObject.getString("content"));
//						}
//					}
					fileName="公证电影"  + ".apk";
					showAlertDialog("qwqwqwfdasdsasdasdasdasd");

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

			@Override
			public void onFailure(Call call, Exception e) {
				// TODO Auto-generated method stub
				fileName="公证电影"  + ".apk";
				showAlertDialog("qwqwqwfdasdsasdasdasdasd");
			}
		});

	}

	protected AlertDialog showAlertDialog(String content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setCancelable(true);
		builder.setMessage(content);
		builder.setTitle("发现新版本");
		builder.setNegativeButton("下次再说", null);
		builder.setPositiveButton("现在更新", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				/** register download success broadcast **/
				mContext.registerReceiver(completeReceiver,
								 new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
				startLoad();
			}
		});

		AlertDialog dialog = builder.show();
		dialog.setCanceledOnTouchOutside(true);
		return dialog;
	}

	private long startLoad() {
		DownloadManager downloadManager = (DownloadManager) mContext
				.getSystemService(Context.DOWNLOAD_SERVICE);
		DownloadManager.Request request = new DownloadManager.Request(
				Uri.parse(apkUrl));
		request.setDestinationInExternalPublicDir(folderName,fileName);
		request.setTitle("通知栏标题");
		request.setDescription("通知栏描述");
//		request.setNotificationVisibility(Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//默认是下载中显示进度，完成后消失
		// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
		// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
		// request.setMimeType("application/cn.trinea.download.file");
		long downloadId = downloadManager.enqueue(request);
		return downloadId;
	}

	/*下载成功监听*/
	class CompleteReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// get complete download id
//			long completeDownloadId = intent.getLongExtra(
//					DownloadManager.EXTRA_DOWNLOAD_ID, -1);
			mContext.unregisterReceiver(completeReceiver);
			AppUtils.install(mContext, FileUtils.getFilePath(folderName, fileName));
		}
	};

}
