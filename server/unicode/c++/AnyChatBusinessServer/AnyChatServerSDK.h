#if !defined(BR_ANYCHAT_SERVER_SDK_H__INCLUDED_)
#define BR_ANYCHAT_SERVER_SDK_H__INCLUDED_


/**
 *	AnyChat Server SDK Include File
 */


#pragma once


#define BRAS_API extern "C" __declspec(dllexport)

#define BRAS_SERVERAPPMSG_CONNECTED		1		///< ����AnyChat�������ɹ�
#define BRAS_SERVERAPPMSG_DISCONNECT	2		///< ��AnyChat�������Ͽ�����

// ��Ƶ�����¼����Ͷ��壨API��BRAS_VideoCallControl ���������OnVideoCallEvent�ص�������
#define BRAS_VIDEOCALL_EVENT_REQUEST    1       ///< ��������
#define BRAS_VIDEOCALL_EVENT_REPLY      2       ///< ��������ظ�
#define BRAS_VIDEOCALL_EVENT_START      3       ///< ��Ƶ���лỰ��ʼ�¼�
#define BRAS_VIDEOCALL_EVENT_FINISH     4       ///< �Ҷϣ����������лỰ


// ������Ӧ�ó�����Ϣ�ص���������
typedef void (CALLBACK* BRAS_OnServerAppMessage_CallBack)(DWORD dwMsg, LPVOID lpUserValue);
// SDK��ʱ���ص��������壨�ϲ�Ӧ�ÿ����ڸûص��д�����ʱ���񣬶�����Ҫ���⿪���̣߳����Ƕ�ʱ����
typedef void (CALLBACK* BRAS_OnTimerEvent_CallBack)(LPVOID lpUserValue);

// �û�������֤�ص���������
typedef DWORD (CALLBACK* BRAS_VerifyUser_CallBack)(IN LPCTSTR lpUserName,IN LPCTSTR lpPassword, OUT LPDWORD lpUserID, OUT LPDWORD lpUserLevel, OUT LPTSTR lpNickName,IN DWORD dwNCLen, LPVOID lpUserValue);
// �û�������뷿��ص���������
typedef DWORD (CALLBACK* BRAS_PrepareEnterRoom_CallBack)(DWORD dwUserId, DWORD dwRoomId, LPCTSTR lpRoomName,LPCTSTR lpPassword, LPVOID lpUserValue);
// �û���¼�ɹ��ص���������
typedef void (CALLBACK* BRAS_OnUserLoginAction_CallBack)(DWORD dwUserId, LPCTSTR szUserName, DWORD dwLevel, LPCTSTR szIpAddr, LPVOID lpUserValue);
// �û�ע���ص���������
typedef void (CALLBACK* BRAS_OnUserLogoutAction_CallBack)(DWORD dwUserId, LPVOID lpUserValue);
// �û����뷿��ص���������
typedef void (CALLBACK* BRAS_OnUserEnterRoomAction_CallBack)(DWORD dwUserId, DWORD dwRoomId, LPVOID lpUserValue);
// �û��뿪����ص���������
typedef void (CALLBACK* BRAS_OnUserLeaveRoomAction_CallBack)(DWORD dwUserId, DWORD dwRoomId, LPVOID lpUserValue);
// �ϲ�ҵ���Զ������ݻص���������
typedef void (CALLBACK* BRAS_OnRecvUserFilterData_CallBack)(DWORD dwUserId, BYTE* lpBuf, DWORD dwLen, LPVOID lpUserValue);
// �յ��û���������ͨ�����ݻص���������
typedef void (CALLBACK* BRAS_OnRecvUserTextMsg_CallBack)(DWORD dwRoomId, DWORD dwSrcUserId, DWORD dwTarUserId, BOOL bSecret, LPCTSTR lpTextMessage, DWORD dwLen, LPVOID lpUserValue);
// ͸��ͨ�����ݻص���������
typedef void (CALLBACK * BRAS_OnTransBuffer_CallBack)(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen, LPVOID lpUserValue);
// ͸��ͨ��������չ�ص���������
typedef void (CALLBACK * BRAS_OnTransBufferEx_CallBack)(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen, DWORD wParam, DWORD lParam, DWORD dwTaskId, LPVOID lpUserValue);
// �ļ�����ص���������
typedef void (CALLBACK * BRAS_OnTransFile_CallBack)(DWORD dwUserId, LPCTSTR lpFileName, LPCTSTR lpTempFilePath, DWORD dwFileLength, DWORD wParam, DWORD lParam, DWORD dwTaskId, LPVOID lpUserValue);
// ������¼��ص���������
typedef void (CALLBACK * BRAS_OnServerRecord_CallBack)(DWORD dwUserId, DWORD dwParam, DWORD dwRecordServerId, DWORD dwElapse, LPCTSTR lpRecordFileName, LPVOID lpUserValue);
// ��Ƶͨ����Ϣ֪ͨ�ص���������
typedef DWORD (CALLBACK * BRAS_OnVideoCallEvent_CallBack)(DWORD dwEventType, DWORD dwSrcUserId, DWORD dwTarUserId, DWORD dwErrorCode, DWORD dwFlags, DWORD dwParam, LPCTSTR lpUserStr, LPVOID lpUserValue);


/**
 *	API ��������
 */
// ���÷�����Ӧ�ó�����Ϣ�ص�����
BRAS_API DWORD BRAS_SetOnServerAppMessageCallBack(BRAS_OnServerAppMessage_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ����SDK��ʱ���ص�������dwElapse����ʱ���������λ��ms��
BRAS_API DWORD BRAS_SetTimerEventCallBack(DWORD dwElapse, BRAS_OnTimerEvent_CallBack lpFunction, LPVOID lpUserValue=NULL);

// �����û�������֤�ص�����
BRAS_API DWORD BRAS_SetVerifyUserCallBack(BRAS_VerifyUser_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û�������뷿��ص�����
BRAS_API DWORD BRAS_SetPrepareEnterRoomCallBack(BRAS_PrepareEnterRoom_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û���¼�ɹ��ص�����
BRAS_API DWORD BRAS_SetOnUserLoginActionCallBack(BRAS_OnUserLoginAction_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û�ע���ص�����
BRAS_API DWORD BRAS_SetOnUserLogoutActionCallBack(BRAS_OnUserLogoutAction_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û����뷿��ص�����
BRAS_API DWORD BRAS_SetOnUserEnterRoomActionCallBack(BRAS_OnUserEnterRoomAction_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û��뿪����ص�����
BRAS_API DWORD BRAS_SetOnUserLeaveRoomActionCallBack(BRAS_OnUserLeaveRoomAction_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û��ϲ�ҵ���Զ������ݻص�����
BRAS_API DWORD BRAS_SetOnRecvUserFilterDataCallBack(BRAS_OnRecvUserFilterData_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����û���������ͨ�����ݻص�����
BRAS_API DWORD BRAS_SetOnRecvUserTextMsgCallBack(BRAS_OnRecvUserTextMsg_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ����͸��ͨ�����ݻص�����
BRAS_API DWORD BRAS_SetOnTransBufferCallBack(BRAS_OnTransBuffer_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ����͸��ͨ��������չ�ص�����
BRAS_API DWORD BRAS_SetOnTransBufferExCallBack(BRAS_OnTransBufferEx_CallBack lpFunction, LPVOID lpUserValue=NULL);
// �����ļ�����ص�����
BRAS_API DWORD BRAS_SetOnTransFileCallBack(BRAS_OnTransFile_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ���÷�����¼��֪ͨ�ص�����
BRAS_API DWORD BRAS_SetOnServerRecordCallBack(BRAS_OnServerRecord_CallBack lpFunction, LPVOID lpUserValue=NULL);
// ������Ƶͨ����Ϣ֪ͨ�ص�����
BRAS_API DWORD BRAS_SetOnVideoCallEventCallBack(BRAS_OnVideoCallEvent_CallBack lpFunction, LPVOID lpUserValue=NULL);


// ��ȡSDK�汾��Ϣ
BRAS_API DWORD BRAS_GetSDKVersion(DWORD& dwMainVer, DWORD& dwSubVer, TCHAR* lpCompileTime, DWORD dwBufLen);
// ��ʼ��SDK
BRAS_API DWORD BRAS_InitSDK(DWORD dwReserved);
// �ͷ���Դ
BRAS_API DWORD BRAS_Release(void);

// ��ָ���û���������
BRAS_API DWORD BRAS_SendBufToUser(DWORD dwUserId, LPCTSTR lpBuf, DWORD dwLen);
// ��ָ������������û���������
BRAS_API DWORD BRAS_SendBufToRoom(DWORD dwRoomId, LPCTSTR lpBuf, DWORD dwLen);

// ��ָ���û�����͸��ͨ������
BRAS_API DWORD BRAS_TransBuffer(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen);
// ��ָ���û�������չ����������
BRAS_API DWORD BRAS_TransBufferEx(DWORD dwUserId, LPBYTE lpBuf, DWORD dwLen, DWORD wParam, DWORD lParam, DWORD dwFlags, DWORD& dwTaskId);
// ��ָ���û������ļ�
BRAS_API DWORD BRAS_TransFile(DWORD dwUserId, LPCTSTR lpLocalPathName, DWORD wParam, DWORD lParam, DWORD dwFlags, DWORD& dwTaskId);

// ���Ķ�¼�����
BRAS_API DWORD BRAS_StreamRecordCtrl(DWORD dwUserId, BOOL bStartRecord, DWORD dwFlags, DWORD dwParam, DWORD dwRecordServerId);
// ��Ƶ�����¼����ƣ����󡢻ظ����Ҷϵȣ�
BRAS_API DWORD BRAS_VideoCallControl(DWORD dwEventType, DWORD dwUserId, DWORD dwErrorCode, DWORD dwFlags=0, DWORD dwParam=0, LPCTSTR lpUserStr=NULL);

#endif //BR_ANYCHAT_SERVER_SDK_H__INCLUDED_