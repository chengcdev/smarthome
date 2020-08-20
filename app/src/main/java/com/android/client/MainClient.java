
package com.android.client;

import android.content.Context;
import android.content.Intent;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.android.CommSysDef;
import com.android.CommTypeDef;
import com.android.Common;
import com.android.FtpSystemParam;
import com.android.IntentDef;
import com.android.IntentDef.PubIntentTypeE;
import com.android.InterCommTypeDef;
import com.android.interf.IFaceListener;
import com.android.main.FaceFtpLogic;
import com.android.main.MainCommDefind;
import com.android.provider.FullDeviceNo;
import com.android.provider.NetworkHelp;
import com.mili.smarthome.tkj.app.App;
import com.mili.smarthome.tkj.appfunc.AppConfig;
import com.mili.smarthome.tkj.appfunc.BuildConfigHelper;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenter;
import com.mili.smarthome.tkj.appfunc.cardfunc.CardPresenterImpl;
import com.mili.smarthome.tkj.dao.DeviceNoRuleSubsectionDao;
import com.mili.smarthome.tkj.dao.DoorReminderDao;
import com.mili.smarthome.tkj.dao.FingerDao;
import com.mili.smarthome.tkj.dao.UserInfoDao;
import com.mili.smarthome.tkj.entities.DoorReminderModel;
import com.mili.smarthome.tkj.entities.FaceMegviiModel;
import com.mili.smarthome.tkj.entities.FaceWffrModel;
import com.mili.smarthome.tkj.entities.userInfo.UserCardInfoModels;
import com.mili.smarthome.tkj.entities.userInfo.UserPwdModels;
import com.mili.smarthome.tkj.face.FaceProtocolInfo;
import com.mili.smarthome.tkj.utils.FileUtils;
import com.mili.smarthome.tkj.utils.LogUtils;
import com.mili.smarthome.tkj.utils.SystemSetUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.android.CommTypeDef.CARD_MAX_LEN;
import static com.android.CommTypeDef.DOOR_REMINDER_MAX_LEN;
import static com.android.CommTypeDef.PWD_MAX_LEN;
import static com.android.IntentDef.PubIntentTypeE.INTENT_MAIN_MQTT_DOORREMINDER_ADD;
import static com.android.IntentDef.PubIntentTypeE.INTENT_MAIN_MQTT_DOORREMINDER_DEL;
import static com.android.IntentDef.PubIntentTypeE.INTENT_MAIN_MQTT_INFO_ADD;
import static com.android.IntentDef.PubIntentTypeE.INTENT_MAIN_MQTT_SYN_TIME;

public class MainClient extends BaseClient implements IntentDef.OnNetCommDataReportListener {

	private static final String tag = "MainClient";
	private Context mContext = null;
	private IFaceListener mFaceListener;
	private InterCommTypeDef.IFaceRecognizeListener mFaceRecognizeListener = null;
	private InterCommTypeDef.IFaceTemperatureListener mFaceTemperatureListener = null;

	private CardPresenter mCardPresenter = new CardPresenterImpl();

	private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
	private ExecutorService mPwdExecutor = Executors.newSingleThreadExecutor();

	private static MainClient mInstance;
	public static MainClient getInstance(){
		if (mInstance == null) {
			synchronized (MainClient.class) {
				if (mInstance == null) {
					mInstance = new MainClient(App.getInstance());
				}
			}
		}
		return mInstance;
	}
	private MainClient(Context context) {
		super(context);
		String[] list=new String[]{IntentDef.MODULE_INFO,IntentDef.MODULE_ELECTRICAL,
				IntentDef.MODULE_INTERCOMM,IntentDef.MODULE_MAIN,IntentDef.MODULE_MEDIA};
		startReceiver(context,list);
		StartIPC_Main(context);
		setmDataReportListener(this);
		mContext = context;
	}

    public void stopMainClient()
    {
        stopReceiver(mContext, IntentDef.MODULE_MAIN);
        StopIPC(mContext, CommSysDef.SERVICE_NAME_MAIN, IntentDef.MODULE_MAIN);
    }

    public void setFaceListener(IFaceListener listener) {
		mFaceListener = listener;
	}

	public void setFaceRecognizeListener(InterCommTypeDef.IFaceRecognizeListener listener) {
		mFaceRecognizeListener = listener;
	}

	/**
	 * 人脸体温监听器
	 * @param listener
	 */
	public void setFaceTemperatureListener(InterCommTypeDef.IFaceTemperatureListener listener) {
		mFaceTemperatureListener = listener;
	}

	public void OnDataReport(String action, int type, final byte[] data) {
		if (!action.equals(IntentDef.MODULE_MAIN))
			return;

		switch (type) {
		case PubIntentTypeE.INTENT_MAIN_SET_SYS_PASS:
			String passwd = Common.byteToString(data);
			//LocalUserInfoModelHelper.getInstnce().addOpenPwd(Constant.MANAGE_CENTER_ROOM_NO, passwd);
			break;

		case PubIntentTypeE.INTENT_MAIN_SET_AREACODEAM:
			int areno = Common.bytes2int(data,0);
			FullDeviceNo mFullDeviceNo1 = new FullDeviceNo(mContext);
			mFullDeviceNo1.setAreaNo(areno);
			break;

		case PubIntentTypeE.INTENT_MAIN_DEVNO_RULE_CMD:
			byte StairNolen = (byte) (data[0] & 0xff);
			byte RoomNolen = (byte) (data[1] & 0xff);
			byte CellNolen = (byte) (data[2] & 0xff);
			byte UseCelllen = (byte) (data[3] & 0xff);
			int Subsection = Common.bytes2int(data,4);

			Log.d(tag, "PublicCommand.CMD_DEVNO_RULE_CMD StairNolen [" + StairNolen + "] RoomNolen [" + RoomNolen
					+ "] CellNolen [" + CellNolen + "] UseCelllen [" + UseCelllen + "] Subsection [" + Subsection + "]");

			String SubString = String.valueOf(Subsection);

			FullDeviceNo mFullDeviceNo = new FullDeviceNo(mContext);
			if(mFullDeviceNo != null){
				mFullDeviceNo.setCellNoLen(CellNolen);
				mFullDeviceNo.setUseCellNo(UseCelllen);
				mFullDeviceNo.setStairNoLen(StairNolen);
				mFullDeviceNo.setRoomNoLen(RoomNolen);
				mFullDeviceNo.setSubsection(Subsection);
				int sublen = SubString.length();

				// 清空
				DeviceNoRuleSubsectionDao deviceNoRuleSubsectionDao = new DeviceNoRuleSubsectionDao();
				deviceNoRuleSubsectionDao.clearModel();
				deviceNoRuleSubsectionDao.addModel(0,SubString.length(),"");

				if (sublen > 0)
				{
					int index = 8;
					String nSubString[] = new String[sublen];
					byte temp[] = new byte[10];
					for (int j = 0; j < sublen; j++){
						System.arraycopy(data,(index+j*10),temp,0,10);
						nSubString[j] = Common.byteToString(temp);

						deviceNoRuleSubsectionDao.addModel(j + 1,0,nSubString[j]);
					}
				}
				Intent mIntent =  new Intent(CommSysDef.BROADCAST_DEVICENORULE);
				Common.SendBroadCast(mContext, mIntent);
			}
			break;

		case PubIntentTypeE.INTENT_MAIN_SYN_TIME:
			short year = (short) Common.bytes2short(data, 0);
			byte month = (byte) (data[2] & 0xff);
			byte day = (byte) (data[3] & 0xff);
			byte week = (byte) (data[4] & 0xff);
			byte hour = (byte) (data[5] & 0xff);
			byte min = (byte) (data[6] & 0xff);
			byte sec = (byte) (data[7] & 0xff);
			String time_str = getUTCtimeStr(year, month, day, hour, min, sec);
			String time_str_local = converTime(time_str, TimeZone.getDefault());
			String str[] = time_str_local.split(" ");
			String date = str[0];
			String time = str[1];
			String datestr[] = date.split("-");
			String timestr[] = time.split(":");
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, Integer.valueOf(datestr[0]));
			c.set(Calendar.MONTH, Integer.valueOf(datestr[1]) - 1);
			c.set(Calendar.DAY_OF_MONTH, Integer.valueOf(datestr[2]));
			c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(timestr[0]));
			c.set(Calendar.MINUTE, Integer.valueOf(timestr[1]));
			c.set(Calendar.SECOND, Integer.valueOf(timestr[2]));
			long when = c.getTimeInMillis();
			if (when / 1000 < Integer.MAX_VALUE) {
				SystemClock.setCurrentTimeMillis(when);
			}
			break;

		case PubIntentTypeE.INTENT_CARD_ADD: {
			int count = data.length / CARD_MAX_LEN;
			UserCardInfoModels[] cardList = new UserCardInfoModels[count];
			for (int i = 0; i < count; i++) {
				byte[] cardData = new byte[CARD_MAX_LEN];
				System.arraycopy(data, i * CARD_MAX_LEN, cardData, 0, CARD_MAX_LEN);
				int cardType = cardData[0];

				byte[] cardStrByte = new byte[10];
				System.arraycopy(cardData, 1, cardStrByte, 0, 10);
				String cardStr = Common.byteToString(cardStrByte);

				byte[] roomStrByte = new byte[20];
				System.arraycopy(cardData, 11, roomStrByte, 0, 20);
				String roomStr = Common.byteToString(roomStrByte);
				int roomNoState = Common.bytes2int(data, 31);
				byte[] keyID = new byte[12];
				System.arraycopy(cardData, 35, keyID, 0, 12);
				String keyIDStr = Common.byteToString(keyID);
				int startTime = Common.bytes2int(data, 47);
				int endTime = Common.bytes2int(data, 51);
				int lifecycle = Common.bytes2int(data, 55);

//				Log.e("addCard", "cardStr = " + cardStr + " roomStr = " + roomStr + " cardType = " + cardType + "  i = " + i);
				UserCardInfoModels cardItem = new UserCardInfoModels();
				cardItem.setCardNo(cardStr);
				cardItem.setRoomNo(roomStr);
				cardItem.setCardType(cardType);
				cardItem.setRoomNoState(roomNoState);
				cardItem.setKeyID(keyIDStr);
				cardItem.setStartTime(startTime);
				cardItem.setEndTime(endTime);
				cardItem.setLifecycle(lifecycle);
				cardList[i] = cardItem;
			}
			mCardPresenter.notifyCardAdd(cardList);
		}
			break;

		case PubIntentTypeE.INTENT_CARD_DEL: {
			int count = data.length / 10;
			String[] cardNos = new String[count];
			for (int i = 0; i < count; i++) {
				byte[] CardByte = new byte[10];
				System.arraycopy(data, i * 10, CardByte, 0, 10);
				String CardStr = Common.byteToString(CardByte);
//				Log.e("delCard", "CardStr = " + CardStr);
				cardNos[i] = CardStr;
			}
			mCardPresenter.notifyCardDel(cardNos);
		}
			break;

		case PubIntentTypeE.INTENT_CARD_CLEAR:
			mCardPresenter.notifyCardClear();
			break;

		case PubIntentTypeE.INTENT_FINGER_ADD: {
			int fingerId = Common.bytes2int(data, 0);
			int valid = Common.bytes2int(data, 4);
			byte[] fingerInfo = new byte[384];
			System.arraycopy(data, 8, fingerInfo, 0, 384);
			byte[] roomStrByte = new byte[20];
			System.arraycopy(data, 4+4+384, roomStrByte, 0, 20);
			String roomStr = Common.byteToString(roomStrByte);

//			Log.e("INTENT_FINGER_ADD", "fingerId = " + fingerId + " valid = " + valid + " roomStr = " + roomStr);

			FingerDao fingerDao = new FingerDao();
			fingerDao.insert(fingerId, valid, fingerInfo, roomStr);
		}
			break;

		case PubIntentTypeE.INTENT_FINGER_DEL: {
			int count = Common.bytes2int(data, 0);
			byte[] fingerIdByte = new byte[count*4];
			System.arraycopy(data, 4, fingerIdByte, 0, count*4);

			Integer[] fingerIds = new Integer[count];
			for(int i=0; i<count; i++) {
				fingerIds[i] = Common.bytes2int(fingerIdByte, i*4);
//				Log.e("INTENT_FINGER_DEL", "fingerId[" + i + "] = " + fingerIds[i]);
			}

			FingerDao fingerDao = new FingerDao();
			fingerDao.deleteByFingerId(fingerIds);
		}
		break;

		case PubIntentTypeE.INTENT_FINGER_DEL_USER: {
			byte[] roomStrByte = new byte[20];
			System.arraycopy(data, 0, roomStrByte, 0, 20);
			String roomStr = Common.byteToString(roomStrByte);

//			Log.e("INTENT_FINGER_DEL_USER", "roomStr = " + roomStr);
			FingerDao fingerDao = new FingerDao();
			fingerDao.deleteByRoomNo(roomStr);
		}
		break;

		case PubIntentTypeE.INTENT_FINGER_CLEAR: {
			FingerDao fingerDao = new FingerDao();
			fingerDao.clear();
		}
		break;

		case PubIntentTypeE.INTENT_MAIN_CENTER_ISONLINE:
			MainCommDefind.center_isonline = (byte) (data[0] & 0xff);
			break;

		case PubIntentTypeE.INTENT_MAIN_FACE_LICENSE:
			int faceLicense = Common.bytes2int(data,0);
			int faceType =  Common.bytes2int(data,4);
			// 1:EI 2:Face++
			Log.e(tag,"faceLicense: "+faceLicense+ " faceType: "+faceType);
			if (mFaceListener != null) {
				mFaceListener.onFaceLicense(faceLicense, faceType);
			}
			break;

		case PubIntentTypeE.INTENT_MAIN_FTP_SYSTEM_PARAM:
			getCenterSystemParam(data);
			break;

		case PubIntentTypeE.IINTENT_MAIN_FACE_REGISTER:
			Log.d(tag, " ========= IINTENT_MAIN_FACE_REGISTER =========" + mFaceListener);
			if (mFaceListener != null) {
				FaceFtpLogic mFaceFtpLogic = new FaceFtpLogic(mFaceListener);
				mFaceFtpLogic.FaceFtpDownLoad(data);
			}
			break;

		case PubIntentTypeE.IINTENT_MAIN_FACE_DEL:
			int delRetsult = Common.bytes2int(data, 0);
			if (delRetsult == 1) {
				byte[] faceToken = new byte[32];
				System.arraycopy(data, 4, faceToken, 0, 32);
				String mFaceToken = Common.byteToString(faceToken);
				Log.d(tag, "Del Face mFaceToken: " + mFaceToken);
				if (mFaceListener != null) {
					mFaceListener.onFaceDelete(mFaceToken);
				}
			}
			break;

		case PubIntentTypeE.INTENT_MAIN_FACE_QUERY_INFO:
			int queryRetsult = Common.bytes2int(data,0);
			if (queryRetsult == 1){
				byte[] roomDevNo = new byte[12];
				System.arraycopy(data, 4, roomDevNo, 0, 12);
			}
			break;

			case PubIntentTypeE.INTENT_MAIN_FACE_COLLECT:
				int collect = Common.bytes2int(data,0);
				if (collect == 0){
					int collectResult = Common.bytes2int(data,4);
					Log.e(tag,"collectResult: "+collectResult);
				}
				else{

				}
				Log.e(tag,"collect: "+collect);
				break;

			case PubIntentTypeE.INTENT_MAIN_MQTT_PASSWD_ADD:
				mPwdExecutor.execute(new Runnable() {
					@Override
					public void run() {
						UserInfoDao userInfoDao = new UserInfoDao();
						int count = data.length/PWD_MAX_LEN;
						int index = 0;
						for (int i = 0; i < count; i++) {
							byte[] pwdNo = new byte[10];
							System.arraycopy(data, index, pwdNo, 0, 10);
							String pwdNoStr = Common.byteToString(pwdNo);
							index += 10;
							byte[] roomNo = new byte[20];
							System.arraycopy(data, index, roomNo, 0, 20);
							String roomNoStr = Common.byteToString(roomNo);
							index += 20;
							int roomNoState = Common.bytes2int(data, index);
							index += 4;
							byte[] keyID = new byte[12];
							System.arraycopy(data, index, keyID, 0, 12);
							String keyIDStr = Common.byteToString(keyID);
							index += 12;
							int attri = Common.bytes2int(data, index);
							index += 4;
							int startTime = Common.bytes2int(data, index);
							index += 4;
							int endTime = Common.bytes2int(data, index);
							index += 4;
							int lifecycle = Common.bytes2int(data, index);
							index += 4;
							UserPwdModels model = new UserPwdModels();
							model.setOpenDoorPwd(pwdNoStr);
							model.setRoomNo(roomNoStr);
							model.setRoomNoState(roomNoState);
							model.setKeyID(keyIDStr);
							model.setAttri(attri);
							model.setStartTime(startTime);
							model.setEndTime(endTime);
							model.setLifecycle(lifecycle);
							userInfoDao.addOpenPwd(model);
						}
					}
				});
				break;

			case PubIntentTypeE.INTENT_MAIN_MQTT_PASSWD_DEL:
				mPwdExecutor.execute(new Runnable() {
					@Override
					public void run() {
						UserInfoDao userInfoDao = new UserInfoDao();
						int mDelPassWDCount = Common.bytes2int(data, 0);
						int delPassWDIndex = 4;

						for (int i = 0; i < mDelPassWDCount; i++) {
							byte[] delPassWDNum = new byte[10];
							System.arraycopy(data, delPassWDIndex, delPassWDNum, 0, 10);
							String mDelPassWDNum = Common.byteToString(delPassWDNum);
							delPassWDIndex += 10;
							byte[] delPassWDRoom = new byte[20];
							System.arraycopy(data, delPassWDIndex, delPassWDRoom, 0, 20);
							String mDelPassWDRoom = Common.byteToString(delPassWDRoom);
							delPassWDIndex += 20;

							userInfoDao.deleteOpenPwd(mDelPassWDRoom, mDelPassWDNum,1);
						}
					}
				});
				break;

			case PubIntentTypeE.INTENT_MAIN_MQTT_FACE_REGISTER:
				mExecutor.execute(new Runnable() {
					@Override
					public void run() {
						int dataIndex = 0;
						int mRegCode = Common.bytes2int(data, dataIndex);
						dataIndex += 4;
						byte[] msgID = new byte[20];
						System.arraycopy(data, dataIndex, msgID, 0, 20);
						String mMsgID = Common.byteToString(msgID);
						dataIndex += 20;
						byte[] faceToken = new byte[32];
						System.arraycopy(data, dataIndex, faceToken, 0, 32);
						String mFaceToken = Common.byteToString(faceToken);
						dataIndex += 32;
						byte[] fileName = new byte[128];
						System.arraycopy(data, dataIndex, fileName, 0, 128);
						String mFileName = Common.byteToString(fileName);
						dataIndex += 128;
						int mRoomNoSate = Common.bytes2int(data, dataIndex);
						dataIndex += 4;
						byte[] keyID = new byte[12];
						System.arraycopy(data, dataIndex, keyID, 0, 12);
						String mKeyID = Common.byteToString(keyID);
						dataIndex += 12;
						byte[] exturl = new byte[128];
						System.arraycopy(data, dataIndex, exturl, 0, 128);
						String mExturl = Common.byteToString(exturl);
						dataIndex += 128;
						int mAttri = Common.bytes2int(data, dataIndex);
						dataIndex += 4;
						int mStartTime = Common.bytes2int(data, dataIndex);
						dataIndex += 4;
						int mEndTime = Common.bytes2int(data, dataIndex);
						dataIndex += 4;
						int mLifecycle = Common.bytes2int(data, dataIndex);
						dataIndex += 4;

						if (mFaceListener != null) {
							FaceProtocolInfo faceProtocolInfo = new FaceProtocolInfo();
							faceProtocolInfo.setFaceFirstName(mFaceToken);
							faceProtocolInfo.setRoomNoState(mRoomNoSate);
							faceProtocolInfo.setKeyID(mKeyID);
							faceProtocolInfo.setExturl(mExturl);
							faceProtocolInfo.setAttri(mAttri);
							faceProtocolInfo.setStartTime(mStartTime);
							faceProtocolInfo.setEndTime(mEndTime);
							faceProtocolInfo.setLifecycle(mLifecycle);
							boolean result = mFaceListener.onFaceEnroll(mFileName, faceProtocolInfo);
							FileUtils.deleteFile(mFileName);
							MainClient.getInstance().Main_FaceMqttPicRegResult(mRegCode, result ? 1 : 0, mMsgID);
						}
					}
				});
				break;

			case PubIntentTypeE.INTENT_MAIN_MQTT_FACE_DEL:
				mExecutor.execute(new Runnable() {
					@Override
					public void run() {
						int mPicCount = Common.bytes2int(data, 0);
						int index = 4;
						if (mFaceListener != null) {
							for (int i = 0; i < mPicCount; i++) {
								byte[] delFaceToken = new byte[32];
								System.arraycopy(data, index, delFaceToken, 0, 32);
								String mDelFaceToken = Common.byteToString(delFaceToken);
								mFaceListener.onFaceDelete(mDelFaceToken);
								index += 32;
							}
						}
					}
				});
				break;

			case INTENT_MAIN_MQTT_DOORREMINDER_ADD:
				mExecutor.execute(new Runnable() {
					@Override
					public void run() {
						DoorReminderDao doorReminderDao = new DoorReminderDao();
						int count = data.length/DOOR_REMINDER_MAX_LEN;
						int index = 0;
						for(int i = 0; i<count; i++){
							int mFlagType = Common.bytes2int(data, 0);
							index += 4;
							byte[] flagID = new byte[32];
							System.arraycopy(data, index, flagID, 0, 32);
							String mFlagID = Common.byteToString(flagID);
							index += 32;
							byte[] voiceText = new byte[128];
							System.arraycopy(data, index, voiceText, 0, 128);
							String mVoiceText = Common.byteToStringUTF8(voiceText);
							index += 128;
							int mStarttime = Common.bytes2int(data, index);
							index += 4;
							int mEndtime = Common.bytes2int(data, index);
							DoorReminderModel model = new DoorReminderModel();
							model.setFlagType(mFlagType);
							model.setFlagID(mFlagID);
							model.setVoiceText(mVoiceText);
							model.setStartTime(mStarttime);
							model.setEndTime(mEndtime);
							doorReminderDao.addDoorReminder(model);
						}
					}
				});
				break;

			case INTENT_MAIN_MQTT_DOORREMINDER_DEL:
				mExecutor.execute(new Runnable() {
					@Override
					public void run() {
						int index = 0;
						DoorReminderDao doorReminderDao = new DoorReminderDao();
						int doorCount = Common.bytes2int(data, index);
						index += 4;
						for (int i = 0; i <doorCount; i++){
							byte[] flagID = new byte[32];
							System.arraycopy(data, index, flagID, 0, 32);
							String mFlagID = Common.byteToString(flagID);
							index += 32;
							doorReminderDao.deleteDoorReminder(mFlagID);
						}
					}
				});
				break;

			case INTENT_MAIN_MQTT_SYN_TIME:
				long curTimeMillis = Common.bytes2int(data, 0)*1000L;
				LogUtils.e("curTimeMillis: "+curTimeMillis);
				if (curTimeMillis / 1000 < Integer.MAX_VALUE) {
					SystemClock.setCurrentTimeMillis(curTimeMillis);
				}
				break;

			case INTENT_MAIN_MQTT_INFO_ADD:
				break;

		case PubIntentTypeE.INTENT_AREA_LOGO:
			String path = Common.byteToString(data);//返回logo路径
			break;

        case PubIntentTypeE.INTENT_MAIN_CLOUD_REBOOT:
        	if (BuildConfigHelper.isGate()) {
        		// 闸机版不处理咚咚相关逻辑
				return;
			}
			LogUtils.d(" =========== INTENT_MAIN_CLOUD_REBOOT ========== ");
			SetDriverSinglechipClient setDriverSinglechipClient = new SetDriverSinglechipClient();
			setDriverSinglechipClient.rebootSystem();
            break;

		case PubIntentTypeE.INTENT_MAIN_CAMERA_ERROR:
			LogUtils.e("Camera Error!!!");
			break;

        case PubIntentTypeE.INTENT_MAIN_FACE_RECOGNIZE:
            int state = Common.bytes2int(data, 0);
            Log.d(tag, " === Face_Recognize_CallBack: state[" + state + "] ====");
            if (mFaceRecognizeListener != null) {
                mFaceRecognizeListener.FaceRecognizeResult(state);
            }
            break;

        case PubIntentTypeE.INTENT_MAIN_FACE_TEMPERATURE:
        	int tempState = Common.bytes2int(data, 0);
			Log.d(tag, " === INTENT_MAIN_FACE_TEMPERATURE: tempState[" + tempState + "] ====");
        	if (mFaceTemperatureListener != null) {
				mFaceTemperatureListener.FaceTemperatureResult(tempState);
			}
        	break;

		default:
			break;
		}
	}

	public void getCenterSystemParam(byte[] data){
		int len = 0;
		Log.e("getCenterSystemParam","FtpSystemParam datalen: "+ data.length);
		boolean rebootDev = false;
		while (len < data.length){
			int paramType = Common.bytes2int(data, len);
			len += 4;
			int paramLen = Common.bytes2int(data, len);
			len += 4;
			switch (paramType) {
				case CommTypeDef.CenterSysParam.UM_VILLAGEID:
					break;

				case CommTypeDef.CenterSysParam.UM_USECLOUD:
//					if (paramLen == 4) {
//						int enable = Common.bytes2int(data, len);
//						int oldCloudFunc = EntranceGuardDao.getCloudTalk();
//                        Log.e("getCenterSystemParam", "FtpSystemParam Cloud: " + enable + ", old is " + oldCloudFunc);
//						if (oldCloudFunc != enable) {
//							EntranceGuardDao.setCloudTalk(enable);
//                            rebootDev = true;
//						}
//					}
					break;

				case CommTypeDef.CenterSysParam.UM_USEFACE:
//					if (paramLen == 4) {
//						int enable = Common.bytes2int(data, len);
//						int oldFaceFunc = AppConfig.getInstance().getFaceModule();
//                        Log.e("getCenterSystemParam", "FtpSystemParam Face: " + enable + ", old is " + oldFaceFunc);
//						if (oldFaceFunc != enable) {
//							AppConfig.getInstance().setFaceModule(enable);
//                            rebootDev = true;
//						}
//					}
					break;

				case CommTypeDef.CenterSysParam.UM_FTPADDR:
					byte[] ftpAddr = new byte[paramLen];
					System.arraycopy(data, len, ftpAddr, 0, paramLen);
					FtpSystemParam.setmFtpIP(Common.byteToString(ftpAddr));
					break;

				case CommTypeDef.CenterSysParam.UM_FTPUSENAME:
					byte[] ftpUserName = new byte[paramLen];
					System.arraycopy(data, len, ftpUserName, 0, paramLen);
					FtpSystemParam.setmFtpUserName(Common.byteToString(ftpUserName));
					break;

				case CommTypeDef.CenterSysParam.UM_FTPUSEPWD:
					byte[] ftpUserPwd = new byte[paramLen];
					System.arraycopy(data, len, ftpUserPwd, 0, paramLen);
					FtpSystemParam.setmFtpPassWord(Common.byteToString(ftpUserPwd));
					break;

				case CommTypeDef.CenterSysParam.UM_FTPPORT:
					byte[] ftpPort = new byte[paramLen];
					System.arraycopy(data, len, ftpPort, 0, paramLen);
					FtpSystemParam.setmFtpPort(Common.byteToString(ftpPort));
					break;

				case CommTypeDef.CenterSysParam.UM_FTPPIC_BLACKLIST:
					break;

				case CommTypeDef.CenterSysParam.UM_FTPPIC_WHITELIST:
					break;

				case CommTypeDef.CenterSysParam.UM_FTPPIC_ROOM:
					break;

				case CommTypeDef.CenterSysParam.UM_FTPPIC_VISITOR:
					break;

				case CommTypeDef.CenterSysParam.UM_FTPPIC_WORKER:
					break;

				case CommTypeDef.CenterSysParam.UM_FTPSNAP_BLACKLIST:
					break;

				case CommTypeDef.CenterSysParam.UM_FTPSNAP_STFANGER:
					break;

				case CommTypeDef.CenterSysParam.UM_FTPSNAP:
					break;
			}
			len += paramLen;
		}

		// 云端和人脸功能与上位机不同时，重启设备
		if (rebootDev) {
            SystemSetUtils.rebootDevice();
        }
	}

	public static String getUTCtimeStr(int year,int month,int day,int hour,int min,int sec){
		String timeString = null;
		String yearString,monthString,daysString,hourString,minString,secString;
		yearString = Integer.toString(year);
		if(month<10){
			monthString = "0"+month;
		}else{
			monthString = Integer.toString(month);
		}
		if(day<10){
			daysString = "0"+day;
		}else{
			daysString = Integer.toString(day);
		}
		if(hour<10){
			hourString = "0"+hour;
		}else{
			hourString = Integer.toString(hour);
		}
		if(min<10){
			minString = "0"+min;
		}else{
			minString = Integer.toString(min);
		}
		if(sec<10){
			secString = "0"+sec;
		}else{
			secString = Integer.toString(sec);
		}
		timeString = yearString+monthString+daysString+hourString+minString+secString;
		return timeString;
	}


	public static String converTime(String srcTime, TimeZone timezone)
	{
	    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	    SimpleDateFormat dspFmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    String convertTime;
	    Date result_date;
	    long result_time = 0;
	    if (null == srcTime)
	    {
	         result_time = System.currentTimeMillis();
	    }
	    else
	    {
	    	try
	         {
	              sdf.setTimeZone(TimeZone.getTimeZone("GMT00:00"));
	              result_date = sdf.parse(srcTime);
	              result_time = result_date.getTime();
	          }
	          catch (Exception e)
	          {
	             result_time = System.currentTimeMillis();
	                dspFmt.setTimeZone(TimeZone.getDefault());
	                convertTime = dspFmt.format(result_time);
	                return convertTime;
	           }
	    }
	    dspFmt.setTimeZone(timezone);
	    convertTime = dspFmt.format(result_time);
	    Log.e("current zone:", "id=" + sdf.getTimeZone().getID()
	            + "  name=" + sdf.getTimeZone().getDisplayName());
	    return convertTime;
	}

    public void setFullDevNo(FullDeviceNo object){
    	if	(mMainService == null)
    		return;
        try {
			mMainService.Main_setFullDevNo(object, "FullDeviceNo");
		} catch (RemoteException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }

    public void setSysNetparam(NetworkHelp object){
    	if	(mMainService == null)
    		return;
        try {
			mMainService.Main_setSysNetParam(object, "NetworkHelp");
		} catch (RemoteException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }

	public long Main_GetRandomSeed() {
		if (mMainService == null) {
			return 0;
		}
		try {
			return mMainService.Main_getRandomSeed();
		} catch (RemoteException e) {
			// TODO: handle exception
			return 0;
		}
	}

	public void Register_Center(String softverString, String hardverString) {
		if (mMainService == null) {
			Log.e("Register_Center", "mMainService is NULL!!!");
			return;
		}
		try {
			mMainService.Main_registerCenter(softverString, hardverString);
		} catch (RemoteException e) {
			// TODO: handle exception
		}
	}

	public void Refresh_Register_Center() {
		if (mMainService == null) {
			return;
		}
		try {
			mMainService.Main_refreshRegisterCenter();
		} catch (RemoteException e) {
			// TODO: handle exception
		}
	}

	public void Main_SendReqSynTime() {
		if (mMainService == null) {
			return;
		}
		try {
			mMainService.Main_reqSynTime();
		} catch (RemoteException e) {
			// TODO: handle exception
			return;
		}
	}

	/**
	 * 格式化外部sd卡
	 * @return 0:成功 1:失败
	 */
	public int Main_FormatExternalSdCard() {
		if (mMainService == null) {
			return 1;
		}
		try {
			return mMainService.Main_formatExternalSdCard();
		} catch (RemoteException e) {
			// TODO: handle exception
			return 1;
		}
	}

	public String Main_getCloudSn() {
		if (mMainService == null) {
			return null;
		}
		try {
			return mMainService.Main_getCloudSn();
		} catch (RemoteException e) {
			// TODO: handle exception
			return null;
		}
	}

	/**
	 * 获取云端注册状态
	 * @return	1-注册成功 其他-注册失败
	 */
	public int Main_getCloudState() {
		if (mMainService == null) {
			return 0;
		}
		try {
			return mMainService.Main_getCloudState();
		} catch (RemoteException e) {
			// TODO: handle exception
			return 0;
		}
	}

	/**
	 * 获取云端重启状态
	 * @return	1-重启 0-不重启
	 */
	public int Main_getCloudReboot() {
		if (mMainService == null) {
			return 0;
		}
		try {
			return mMainService.Main_getCloudReboot();
		} catch (RemoteException e) {
			// TODO: handle exception
			return 0;
		}
	}

	/**
	 * 人脸照片注册结果
	 */
	public void Main_FacePicRegResult(int regCode, int regResult, String faceToken) {
		if (mMainService == null) {
			return ;
		}
		try {
			mMainService.Main_facePicRegResult(regCode, regResult, faceToken);
		} catch (RemoteException e) {
			return ;
		}
	}

	/**
	 * 设置人脸类型
	 * @param faceType
	 * 0: 无人脸算法 1: EI2.1（离线）2: EI3.1（离线）
	 * 3:Face++离线	4: Face++在线
	 */
	public void Main_SetFaceType(int faceType) {
		if (mMainService == null) {
			return ;
		}
		try {
			mMainService.Main_setFaceType(faceType);
		} catch (RemoteException e) {
			return ;
		}
	}

	/**
	 * 设置人脸类型
	 * @param state
	 * 0: 不在备份状态 1：在备份状态
	 */
	public void Main_SetFaceBackUpState(int state) {
		if (mMainService == null) {
			return ;
		}
		try {
			mMainService.Main_setFaceBackUpState(state);
		} catch (RemoteException e) {
			return ;
		}
	}

	/**
	 * Mqtt人脸照片注册结果
	 */
	public void Main_FaceMqttPicRegResult(int regCode, int regResult, String regMsgid) {
		if (mMainService == null) {
			return ;
		}
		try {
			mMainService.Main_faceMqttPicRegResult(regCode, regResult, regMsgid);
		} catch (RemoteException e) {
			return ;
		}
	}


	/**
	 * @param photName   人脸图片路径
	 * return            0：识别成功 -1：识别失败
	 */
	public int Main_FaceRecognize(String photName) {
		if (mMainService == null)
			return -1;
		try {
			return mMainService.Main_faceHttpRecognize(photName);
		} catch (RemoteException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * 陌生人脸抓拍
	 * @param snapFile   人脸图片路径
	 */
	public void Main_FaceSnapStranger(String snapFile) {
		if (mMainService == null)
			return;
		try {
			mMainService.Main_faceSnapStranger(snapFile);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始人脸体温检测
	 * @return	-1 失败 0 成功
	 */
	public int Main_FaceTempScanStart() {
		if (mMainService == null)
			return -1;
		try {
			mMainService.Main_faceTempScanStart();
			return 0;
		} catch (RemoteException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * face++人脸授权
	 */
	public void Main_FaceLicense() {
		if (mFaceListener != null) {
			mFaceListener.onFaceLicense(1, 2);
		}
	}

	/**
	 * 获取mlink平台注册状态
	 */
	public int Main_GetMlinkState() {
		if (mMainService == null)
			return -1;
		try {
			return mMainService.Main_getMlinkState();
		} catch (RemoteException e) {
			e.printStackTrace();
			return -1;
		}
	}
}
