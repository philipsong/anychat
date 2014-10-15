package com.example.helloanychat;

import java.util.ArrayList;
import java.util.List;

import com.bairuitech.anychat.AnyChatBaseEvent;
import com.bairuitech.anychat.AnyChatCoreSDK;
import com.bairuitech.anychat.AnyChatDefine;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements AnyChatBaseEvent {
	private ListView mRoleList;
	private EditText mEditIP;
	private EditText mEditPort;
	private EditText mEditName;
	private EditText mEditRoomID;
	private TextView mBottomConnMsg;
	private TextView mBottomBuildMsg;
	private Button 	 mBtnStart;
	private Button   mBtnLogout;
	private Button   mBtnWaiting;	
	private LinearLayout mWaitingLayout;
	private LinearLayout mProgressLayout;
	
	private String   mStrIP = "demo.anychat.cn";
	private String   mStrName = "name";
	private int      mSPort = 8906;
	private int      mSRoomID = 1;

	private List<RoleInfo> mRoleInfoList = new ArrayList<RoleInfo>();
	private RoleListAdapter mAdapter;

	private boolean bNeedRelease = false;
	private int UserselfID;
	
	public AnyChatCoreSDK 	anyChatSDK;
	public ConfigEntity 	configEntity;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);

		InitSDK();
		InitLayout();
		//读取登陆配置表
		readLoginDate();
		//初始化登陆配置数据
		initLoginConfig();
	}

	private void InitSDK() {
		if (anyChatSDK == null) {
			anyChatSDK = AnyChatCoreSDK.getInstance(this);
			anyChatSDK.SetBaseEvent(this);
			anyChatSDK.InitSDK(android.os.Build.VERSION.SDK_INT, 0);

			configEntity = new ConfigEntity();
			// 视频采集驱动设置
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_LOCALVIDEO_CAPDRIVER, configEntity.videoCapDriver);
			// 视频显示驱动设置
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_VIDEOSHOW_DRIVERCTRL, configEntity.videoShowDriver);
			// 音频播放驱动设置
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_AUDIO_PLAYDRVCTRL, configEntity.audioPlayDriver);
			// 音频采集驱动设置
			AnyChatCoreSDK.SetSDKOptionInt(
					AnyChatDefine.BRAC_SO_AUDIO_RECORDDRVCTRL, configEntity.audioRecordDriver);

			bNeedRelease = true;
		}
	}

	private void InitLayout() {
		mRoleList = (ListView) this.findViewById(R.id.roleListView);
		mEditIP = (EditText) this.findViewById(R.id.mainUIEditIP);
		mEditPort = (EditText) this.findViewById(R.id.mainUIEditPort);
		mEditName = (EditText) this.findViewById(R.id.main_et_name);
		mEditRoomID = (EditText) this.findViewById(R.id.mainUIEditRoomID);
		mBottomConnMsg = (TextView) this.findViewById(R.id.mainUIbottomConnMsg);
		mBottomBuildMsg = (TextView) this.findViewById(R.id.mainUIbottomBuildMsg);
		mBtnStart = (Button) this.findViewById(R.id.mainUIStartBtn);
		mBtnLogout = (Button) this.findViewById(R.id.mainUILogoutBtn);
		mBtnWaiting = (Button) this.findViewById(R.id.mainUIWaitingBtn);
		mWaitingLayout = (LinearLayout)this.findViewById(R.id.waitingLayout);
		
		mRoleList.setDivider(null);
		mBottomConnMsg.setText("No content to the server");
		// 初始化bottom_tips信息
		mBottomBuildMsg.setText(" V" + anyChatSDK.GetSDKMainVersion()
				+ "."	+ anyChatSDK.GetSDKSubVersion() + "  Build time: "
				+ anyChatSDK.GetSDKBuildTime());
		mBottomBuildMsg.setGravity(Gravity.CENTER_HORIZONTAL);
		mBtnStart.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (checkInputData()) {
					setBtnVisible(ConfigEntity.showWaitingFlag);
					showWaitingTips();
					mSRoomID = Integer.parseInt(mEditRoomID.getText().toString().trim());
					mStrName = mEditName.getText().toString().trim();
					mStrIP = mEditIP.getText().toString().trim();
					mSPort = Integer.parseInt(mEditPort.getText().toString().trim());
					
					anyChatSDK.Connect(mStrIP, mSPort);
					anyChatSDK.Login(mStrName, "");
				}
			}
		});

		mBtnLogout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setBtnVisible(ConfigEntity.showLoginFlag);
				
				anyChatSDK.Logout();
				mRoleList.setAdapter(null);
				mBottomConnMsg.setText("No connnect to the server");
			}
		});
	}
	
	private void initLoginConfig()
	{
		mEditIP.setText(mStrIP);
		mEditName.setText(mStrName);
		mEditPort.setText(String.valueOf(mSPort));
		mEditRoomID.setText(String.valueOf(mSRoomID));
	}
	//读取登陆数据
	private void readLoginDate()
	{
		SharedPreferences preferences = getSharedPreferences("LoginInfo", 0);
		mStrIP = preferences.getString("UserIP", "demo.anychat.cn");
		mStrName = preferences.getString("UserName", "name");
		mSPort = preferences.getInt("UserPort", 8906);
		mSRoomID = preferences.getInt("UserRoomID", 1);
	}
	
	//保存登陆相关数据
	private void saveLoginData()
	{
		SharedPreferences preferences = getSharedPreferences("LoginInfo", 0);
		Editor preferencesEditor = preferences.edit();
		preferencesEditor.putString("UserIP", mStrIP);
		preferencesEditor.putString("UserName", mStrName);
		preferencesEditor.putInt("UserPort", mSPort);
		preferencesEditor.putInt("UserRoomID", mSRoomID);
		preferencesEditor.commit();
	}
	
	private boolean checkInputData() {
		String ip = mEditIP.getText().toString().trim();
		String port = mEditPort.getText().toString().trim();
		String name = mEditName.getText().toString().trim();
		String roomID = mEditRoomID.getText().toString().trim();

		if (ValueUtils.isStrEmpty(ip)) {
			mBottomConnMsg.setText("请输入IP");
			return false;
		} else if (ValueUtils.isStrEmpty(port)) {
			mBottomConnMsg.setText("请输入端口号");
			return false;
		} else if (ValueUtils.isStrEmpty(name)) {
			mBottomConnMsg.setText("请输入姓名");
			return false;
		} else if (ValueUtils.isStrEmpty(roomID)) {
			mBottomConnMsg.setText("请输入房间号");
			return false;
		} else {
			return true;
		}
	}

	//控制登陆，等待和登出按钮状态
	private void setBtnVisible(int index) {
		if (index == ConfigEntity.showLoginFlag) {
			mBtnStart.setVisibility(View.VISIBLE);
			mBtnLogout.setVisibility(View.GONE);
			mBtnWaiting.setVisibility(View.GONE);
		} else if (index == ConfigEntity.showWaitingFlag) {
			mBtnStart.setVisibility(View.GONE);
			mBtnLogout.setVisibility(View.GONE);
			mBtnWaiting.setVisibility(View.VISIBLE);
		} else if (index == ConfigEntity.showLogoutFlag) {
			mBtnStart.setVisibility(View.GONE);
			mBtnLogout.setVisibility(View.VISIBLE);
			mBtnWaiting.setVisibility(View.GONE);
		}
	}
	
	//init登陆等待状态UI
	private void showWaitingTips()
	{		
		mProgressLayout = new LinearLayout(this);
		mProgressLayout.setOrientation(LinearLayout.HORIZONTAL);
		mProgressLayout.setGravity(Gravity.CENTER_VERTICAL);
		mProgressLayout.setPadding(1, 1, 1, 1);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 5, 5, 5);
		ProgressBar progressBar = new ProgressBar(this, null,
				android.R.attr.progressBarStyleLarge);
		mProgressLayout.addView(progressBar, params);

		mWaitingLayout.addView(mProgressLayout, new LayoutParams(params));
	}

	private void hideKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			imm.hideSoftInputFromWindow(getCurrentFocus()
					.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	protected void onDestroy() {
		if (bNeedRelease) {
			anyChatSDK.Release();
		}
		anyChatSDK.Logout();
		super.onDestroy();
	}

	protected void onResume() {
		anyChatSDK.SetBaseEvent(this);
		super.onResume();
	}

	@Override
	public void OnAnyChatConnectMessage(boolean bSuccess) {
		if (!bSuccess) {
			setBtnVisible(ConfigEntity.showLoginFlag);
			mProgressLayout.setVisibility(View.GONE);
			mBtnStart.setClickable(true);
			Toast.makeText(this, "连接服务器失败，自动重连，请稍后...", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void OnAnyChatLoginMessage(int dwUserId, int dwErrorCode) {
		if (dwErrorCode == 0) {
			saveLoginData();			
			setBtnVisible(ConfigEntity.showLogoutFlag);
			mProgressLayout.setVisibility(View.GONE);
			hideKeyboard();
			
			Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
			mBottomConnMsg.setText("Connect to the server success.");
			bNeedRelease = false;
			int sHourseID = Integer.valueOf(mEditRoomID.getEditableText().toString());
			anyChatSDK.EnterRoom(sHourseID, "");
			
			UserselfID = dwUserId;
			// finish();
		} else {
			setBtnVisible(ConfigEntity.showLoginFlag);
			Toast.makeText(this, "登录失败，错误代码：" + dwErrorCode, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void OnAnyChatEnterRoomMessage(int dwRoomId, int dwErrorCode) {
		System.out.println("getEditableText()");
	}

	@Override
	public void OnAnyChatOnlineUserMessage(int dwUserNum, int dwRoomId) {
		mRoleInfoList.clear();
		int[] userID = anyChatSDK.GetOnlineUser();
		RoleInfo userselfInfo = new RoleInfo();
		userselfInfo.setName(anyChatSDK.GetUserName(UserselfID)+"(自己)");
		userselfInfo.setUserID(String.valueOf(UserselfID));
		mRoleInfoList.add(userselfInfo);
		
		for (int index = 0; index < userID.length; ++index) {
			RoleInfo info = new RoleInfo();
			info.setName(anyChatSDK.GetUserName(userID[index]));
			info.setUserID(String.valueOf(userID[index]));
			mRoleInfoList.add(info);
		}

		mAdapter = new RoleListAdapter(MainActivity.this, mRoleInfoList);
		mRoleList.setAdapter(mAdapter);
		mRoleList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,	int arg2, long arg3) {
				if (arg2==0)
					return;
				
				onSelectItem(arg2);
			}
		});
	}

	private void onSelectItem(int postion) {
		String strUserID = mRoleInfoList.get(postion).getUserID();
		Intent intent = new Intent();
		intent.putExtra("UserID", strUserID);
		intent.setClass(this, VideoActivity.class);
		startActivity(intent);
	}

	@Override
	public void OnAnyChatUserAtRoomMessage(int dwUserId, boolean bEnter) {
		System.out.println("OnAnyChatUserAtRoomMessage");
		if (bEnter) {
			RoleInfo info = new RoleInfo();
			info.setUserID(String.valueOf(dwUserId));
			info.setName(anyChatSDK.GetUserName(dwUserId));
			mRoleInfoList.add(info);
			mAdapter.notifyDataSetChanged();
		} else {

			for (int i = 0; i < mRoleInfoList.size(); i++) {
				if (mRoleInfoList.get(i).getUserID().equals("" + dwUserId)) {
					mRoleInfoList.remove(i);
					mAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	@Override
	public void OnAnyChatLinkCloseMessage(int dwErrorCode) {
		setBtnVisible(ConfigEntity.showLoginFlag);
		mRoleList.setAdapter(null);
		Toast.makeText(this, "连接关闭，error：" + dwErrorCode, Toast.LENGTH_SHORT)
				.show();
	}

}