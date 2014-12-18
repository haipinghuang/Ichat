package com.hai.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.filetransfer.FileTransferListener;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferRequest;
import org.jivesoftware.smackx.filetransfer.IncomingFileTransfer;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.ichat.config.MyConfig;
import com.ichat.context.MyContext;
import com.ichat.mode.ImageMsg;
import com.ichat.mode.ImageMsg.Type;
import com.ichat.util.CommonUtils;
import com.ichat.util.Out;

public class FileReceiverService extends Service {
	private MyContext myContext;
	private boolean externalStorge = false;
	@Override
	public void onCreate() {
		myContext = (MyContext) getApplication();
		externalStorge = CommonUtils.isExitsSdcard();
		super.onCreate();
	}
	@Override
	public void onStart(Intent intent, int startId) {
		Out.println("service start");
		Date date=new Date();
		FileTransferManager manager = new FileTransferManager(
				myContext.getConn());
		manager.addFileTransferListener(new FileTransferListener() {
			@Override
			public void fileTransferRequest(FileTransferRequest ftr) {
				Out.println("接收到文件传输|"+ftr.getMimeType()+"|"+ftr.getRequestor());
				IncomingFileTransfer ift = ftr.accept();
				String fileName = ift.getFileName();
				Date date=new Date();
				String preName=CommonUtils.getFormatDate(date);
				File file = new File("/sdcard/"+preName+fileName);
				ImageMsg imageMsg=new ImageMsg(Type.init,file.getAbsolutePath());
				imageMsg.setType(ImageMsg.Type.init);
//				sendBroadcast(imageMsg);
				if (!file.exists()) {
					try {
						file.createNewFile();
					} catch (IOException e) {
						Out.println("文件创建失败...");
						e.printStackTrace();
						ift.cancel();
						ftr.reject();
						return;
					}
				}
				try {
					ift.recieveFile(file);
				} catch (XMPPException e) {
					e.printStackTrace();
					Out.println("文件接收失败...");
					return;
				}finally{
					ift.cancel();
				}
				imageMsg.setType(Type.receive_finish);
				sendBroadcast(imageMsg);
				Out.println("文件接收成功...");
			}
			private void sendBroadcast(ImageMsg imageMsg) {
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				bundle.putSerializable("imageMsg", imageMsg);
				intent.putExtras(bundle);
				intent.setAction(MyConfig.RECEIVE_IMG_ACTION);
				LocalBroadcastManager.getInstance(FileReceiverService.this).sendBroadcast(intent);
			}
		});
	}

	@Override
	public void onDestroy() {
		Out.println("service onDestroy()");
		super.onDestroy();
	}
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

}
