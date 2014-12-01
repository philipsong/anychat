package com.example.anychatfeatures;

import java.util.ArrayList;
import java.util.HashMap;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;
import com.example.anychatfeatures.R;
import com.example.common.CustomApplication;
import com.example.config.ConfigEntity;
import com.example.config.ConfigService;
import com.example.funcActivity.VideoConfig;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FuncMenu extends Activity implements AnyChatBaseEvent {
	public static final int FUNC_VOICEVIDEO = 1;
	public static final int FUNC_TEXTCHAT = 2;
	public static final int FUNC_ALPHACHANNEL = 3;
	public static final int FUNC_FILETRANSFER = 4;
	public static final int FUNC_VIDEO = 5;
	public static final int FUNC_PHOTOGRAPH = 6;
	public static final int FUNC_VIDEOCALL = 7;
	public static final int FUNC_CONFIG = 8;

	// 视频配置界面标识
	public static final int ACTIVITY_ID_VIDEOCONFIG = 1;
	
	private ImageButton mImgBtnReturn;	// 返回
	private TextView mTitleName;
	private GridView mMenuGridView;
	private CustomApplication mCustomApplication;
	private ArrayList<HashMap<String, Object>> mArrItem;// 存储功能菜单图标和描述
	
	AnyChatCoreSDK anyChatSDK;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.funcmenu);
		
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);  
		
		anyChatSDK = AnyChatCoreSDK.getInstance(this);
		anyChatSDK.SetBaseEvent(this);
		
		InitLayout();
		registerBoradcastReceiver();
	}

	private void InitLayout() {
		this.setTitle("AnyChat功能菜单");
		mCustomApplication = (CustomApplication) getApplication();
		mMenuGridView = (GridView) this.findViewById(R.id.funcmenuGridView);
		mImgBtnReturn = (ImageButton) this.findViewById(R.id.returnImgBtn);
		mTitleName = (TextView) this.findViewById(R.id.titleName);

		String[] arrFuncNames = { getString(R.string.voiceVideoInteraction),
				getString(R.string.textChat), getString(R.string.alphaChannel),
				getString(R.string.fileTransfer), getString(R.string.video),
				getString(R.string.photograph), getString(R.string.videoCall),
				getString(R.string.config)};

		int[] arrFuncIcons = { R.drawable.voicevideo, R.drawable.textchat,
				R.drawable.alphachannel, R.drawable.filetransfer,
				R.drawable.video, R.drawable.photograph, R.drawable.videocall,
				R.drawable.config};

		mArrItem = new ArrayList<HashMap<String, Object>>();
		for (int index = 0; index < arrFuncNames.length; ++index) {
			HashMap<String, Object> itemMap = new HashMap<String, Object>();
			itemMap.put("ItemImage", arrFuncIcons[index]);
			itemMap.put("ItemText", arrFuncNames[index]);
			mArrItem.add(itemMap);
		}

		SimpleAdapter sMenuItemAdapter = new SimpleAdapter(this, mArrItem,
				R.layout.funcmenu_cell,
				new String[] { "ItemImage", "ItemText" }, new int[] {
						R.id.funcItemImage, R.id.funcItemText });

		mMenuGridView.setAdapter(sMenuItemAdapter);
		mMenuGridView.setOnItemClickListener(onListClickListener);
		mTitleName.setText(R.string.str_funcTitle);
		mImgBtnReturn.setOnClickListener(onBtnClick);
	}

	OnItemClickListener onListClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			//设置参数部分
			if (arg2 + 1 == FUNC_CONFIG)
			{
				Intent intent = new Intent();
				intent.setClass(FuncMenu.this, VideoConfig.class);
				startActivityForResult(intent, ACTIVITY_ID_VIDEOCONFIG);
				
				return;
			}
			
			// 不同的功能模块进入不同的房间，从1开始，1~7房间，如语音视频聊天进入1号房间，文字聊天进入2号房间。。。
			mCustomApplication.setCurOpenFuncUI(arg2 + 1);
			openRolesListActivity(arg2 + 1);
		}
	};
	
	OnClickListener onBtnClick = new OnClickListener() {
		@Override
		public void onClick(View v) {
			
			destroyCurActivity();
		}
	};
	// 音视频交互
	private void openRolesListActivity(int sEnterRoomID) {
		Intent intent = new Intent(this, RolesListActivity.class);
		intent.putExtra("roomID", sEnterRoomID);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
				| Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (resultCode == RESULT_OK && requestCode == ACTIVITY_ID_VIDEOCONFIG ) {
			ApplyVideoConfig();
		}
	}

	// 根据配置文件配置视频参数
	private void ApplyVideoConfig() {
		ConfigEntity configEntity = ConfigService.LoadConfig(this);
		if (configEntity.mConfigMode == 1) // 自定义视频参数配置
		{
			// 设置本地视频编码的码率（如果码率为0，则表示使用质量优先模式）
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_BITRATECTRL,
					configEntity.mVideoBitrate);
			if (configEntity.mVideoBitrate == 0) {
				// 设置本地视频编码的质量
				AnyChatCoreSDK.SetSDKOptionInt(
						AnyChatDefine.BRAC_SO_LOCALVIDEO_QUALITYCTRL,
						configEntity.mVideoQuality);
			}
			// 设置本地视频编码的帧率
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_FPSCTRL,
					configEntity.mVideoFps);
			// 设置本地视频编码的关键帧间隔
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_GOPCTRL,
					configEntity.mVideoFps * 4);
			// 设置本地视频采集分辨率
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_WIDTHCTRL,
					configEntity.mResolutionWidth);
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_HEIGHTCTRL,
					configEntity.mResolutionHeight);
			// 设置视频编码预设参数（值越大，编码质量越高，占用CPU资源也会越高）
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_PRESETCTRL,
					configEntity.mVideoPreset);
		}
		// 让视频参数生效
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_APPLYPARAM,
				configEntity.mConfigMode);
		// P2P设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_NETWORK_P2PPOLITIC,
				configEntity.mEnableP2P);
		// 本地视频Overlay模式设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_OVERLAY,
				configEntity.mVideoOverlay);
		// 回音消除设置
		AnyChatCoreSDK.SetSDKOptionInt(AnyChatDefine.BRAC_SO_AUDIO_ECHOCTRL,
				configEntity.mEnableAEC);
		// 平台硬件编码设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_CORESDK_USEHWCODEC,
				configEntity.mUseHWCodec);
		// 视频旋转模式设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_ROTATECTRL,
				configEntity.mVideoRotateMode);
		// 本地视频采集偏色修正设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_FIXCOLORDEVIA,
				configEntity.mFixColorDeviation);
		// 视频GPU渲染设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_VIDEOSHOW_GPUDIRECTRENDER,
				configEntity.mVideoShowGPURender);
		// 本地视频自动旋转设置
		AnyChatCoreSDK.SetSDKOptionInt(
				AnyChatDefine.BRAC_SO_LOCALVIDEO_AUTOROTATION,
				configEntity.mVideoAutoRotation);
	}
	
	// 广播
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals("NetworkDiscon")) {
				destroyCurActivity();
			}
		}
	};

	public void registerBoradcastReceiver() {
		IntentFilter myIntentFilter = new IntentFilter();
		myIntentFilter.addAction("NetworkDiscon");
		// 注册广播
		registerReceiver(mBroadcastReceiver, myIntentFilter);
	}

	private void destroyCurActivity() {
		this.setResult(RESULT_OK);
		
		onPause();
		onDestroy();
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
		anyChatSDK.SetBaseEvent(this);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK){
			this.setResult(RESULT_OK);			
		}		

		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void OnAnyChatConnectMessage(boolean bSuccess) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		// 销毁当前界面
		destroyCurActivity();
		Intent mIntent = new Intent("NetworkDiscon");
		// 发送广播
		sendBroadcast(mIntent);
	}
}
