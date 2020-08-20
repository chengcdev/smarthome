
package com.wf;

public class wffrjni {


    static {
        System.loadLibrary("wffr");
        System.loadLibrary("wffrjni");

    }

    public wffrjni() {
    }

    public static native int SetVerbose(String path, int verbose);

    public static native int setAndroidVerbose(int verbose); 	// Set 0 to disable all prints, set 1 to enable prints

    public static native int EnableImageSaveForDebugging(int enableSaving); // Set 1 to enable image saving in Basepath location for debugging.

    public static native String GetVersionInfo();

    public static native int VerifyLic(String path);

    public static native int setdbbasepath(String path); //optional API to set base path for database "wffrdb". It should be set before initialize() API to take effect

    public static native int initialize(String path, int width, int height, int widthStep, int frmode, int spoofing);

    public static native int addRecord(String name, String lastName);

    public static native int getLastAddedRecord();	// Return recordID of last added record in DB using API addRecord. If it is -1 then there was no record added.

    public static native Object[] getDbNameList();

    public static native int[] getDbRecordList(int size);

    public static native int DeleteDatabase();

    public static native int DeletePersonFromDb(int recordID);

    public static native int DeletePersonByNameFromDb(String firstname, String lastname);

    public static native int DeletePersonByFirstNameFromDb(String firstname);

    public static native int ExtractPersonByName(String firstname, String lastname);				// Extract a person from "wffrdb" by name to extraction folder "wffrdbExtract".

    public static native int SetSpoofingSensitivity(int senstivity);

    public static native int GetSpoofingSensitivity();

    public static native int GetSpoofingStatus();

    public static native int Release();

    public static native int[][] recognize(byte[] frameByteArray, int width, int height);

    public static native int[][] detectRecognizeMultiThread(byte[] frameByteArray, int width, int height);

    public static native int SetLastRecImageFormatMultiThread(int imageformat);				// Set format of last recognized image for detectRecognizeMultiThread. 0 for Gray, 1 for NV21
    public static native byte[] GetLastRecImageMultiThread(int width, int height, int imageFormat);	// Get last recognized image buffer for detectRecognizeMultiThread.
    public static native int[][] GetLastRecResultsMultiThread();					// Get coordinates of face for last recognized image for detectRecognizeMultiThread.

    public static native int[][] recognizeFromImageFile(String imageFileName);

    public static native float[] confidenceValues();

    public static native String[] nameValues();

    public static native float GetRecognitionThreshold();

    public static native int SetRecognitionThreshold(float threshold);

    public static native int[][] enroll(byte[] frameByteArray, int width, int height);

    public static native int[][] enrollFromImageFile(String imageFileName);

    public static native int[][] enrollFromJpegBuffer(byte[] jpegByteArray, int jpegByteSize);			// Enroll from image jpeg buffer.

    public static native int[][] VerifyFrameForEnroll(byte[] frameByteArray, int width, int height);		// verify if the image is suitable for enrollment using frame pixel buffer
    public static native int[][] VerifyImageForEnrollJpegBuffer(byte[] jpegByteArray, int jpegByteSize);	// verify if the image is suitable for enrollment using jpeg buffer

    public static native int[][] recognizeSingleCamSpoof(byte[] frameByteArray, int width, int height);		// Recognize from single cam with stable face spoof

    public static native int[][] enrollSingleCamSpoof(byte[] frameByteArray, int width, int height);		// Enroll from single cam with stable face spoof
	
    public static native int[][] recognizeDualcam(byte[] frameByteArrayColor, byte[] frameByteArrayIR, int width, int height);	// Recognize from dual camera - color+IR

    public static native int[][] enrollDualcam(byte[] frameByteArrayColor, byte[] frameByteArrayIR, int width, int height);	// Enroll from

    public static native int GetMinFaceDetectionSizePercent();

    public static native int SetMinFaceDetectionSizePercent(int minFaceSize);	// Should be set before initialize() API is called

    public static native int GetDetectionOnlyMode();

    public static native int SetDetectionOnlyMode(int runDetectionOnly);

    public static native float GetSingleCamSpoofThreshold();

    public static native int SetSingleCamSpoofThreshold(float spoofThresh);	//Set single cam spoof threshold. Default is -5.0. Range is [-30.0f, 30.0]. Decreasing threshold will increase sensitivity

    public static native int getSaveDetectedFaceFlag();

    public static native int setSaveDetectedFaceFlag(int saveDetectedFaces);

    public static native int SetUpdateFromPCDB(int enablePCDB, int maxPCDBCount);

    public static native int GetUpdateFromPCDB();

    public static native int SetDeleteExistingNamePCDBUpdate(int enableDelete);		// Enable / Disable deletion of existing ID's with same name in wffrdb database when running PCDB update.
    public static native int GetDeleteExistingNamePCDBUpdate();

    public static native int SetDeleteExistingNameInEnrolling(int enableDelete);	// Enable / Disable deletion of existing ID's with same name in wffrdb for enrolling on device
    public static native int GetDeleteExistingNameInEnrolling();

    public static native int SetEnrollQualityCheckFlag(int enableCheck);		// Enable / Disable face quality check for enrolling like face angle should be frontal and not blurred.
    public static native int GetEnrollQualityCheckFlag();

    public static native int SetRecogQualityCheckFlag(int enableCheck);		// Enable / Disable face quality check for recognition like face angle should be frontal and not blurred.
    public static native int GetRecogQualityCheckFlag();

    public static native int SetOnlineLicensing(int enable);			// Enable / Disable online automatic license generation on device.
    public static native int GetOnlineLicensingFlag();

    public static native String getSaveDetectedImageName();	// Returns the name of the detected face image stored on disk. It should be called after recognize API.

    public static native String getDetectedWfgName();		// Returns the name of the detected face WFG image stored on disk. It should be called after recognize API.

    public static native int getSaveEnrollImagesStatus();

    public static native int saveEnrollImages(int enableSaving);

    public static native byte[] rotateImage(byte[] frameByteArray, int width, int height, int imageFormat, int rotAngle);		//Rotate input image by 90, 180 or 270 degrees. 
																	//imageFormat: 0-Gray, 1-NV21/NV12, 2-YV12

    public static native byte[] rotateImageAndMirror(byte[] frameByteArray, int width, int height, int imageFormat, int rotAngle);	//Rotate input image by 90, 180 or 270 degrees followed by mirror image about Y axis (left-right flip). imageFormat: 0-Gray, 1-NV21/NV12, 2-YV12

    public static native byte[] swapImagePixels(byte[] frameByteArray, int width, int height);		// Swap pixels in Y channel like input pixels {y1,y2,y3,y4,y5,y6...} to {y2,y1,y4,y3,y6,y5...}

    public static native byte[] resizeImage(byte[] frameByteArray, int inpwidth, int inpheight, int outwidth, int outheight);	

    public static native int lowLightDetection(byte[] frameByteArray, int width, int height, int lightStrength);	// Detect low light condition in image. External light can be illumiated in case of low lights. lightStrength Range [0,10], Default = 5.

    /////////////////////////////// PC / Server Enrolling API's ////////////////////////////
    public static native int GetVersionInfoPC();
    public static native int initializeEnrollPC(String path, String firstname, String lastname, int spoofing);
    public static native int ReleaseEnrollPC();
    public static native int[][] enrollPC(byte[] frameByteArray, int width, int height);
    public static native float[] confidenceValuesEnrollPC();
    public static native int SetSpoofingSensitivityEnrollPC(int senstivity);
    public static native int GetSpoofingSensitivityEnrollPC();
    public static native int verifyEnrollFromFilePC(String imageFileName);
    public static native int SetEnrollQualityCheckFlagPC(int  enableCheck);		// Enable / Disable face quality check for enrolling like face angle should be frontal and not blurred.
    public static native int GetEnrollQualityCheckFlagPC();
    //////////////////////////////// PC / Server Enrolling API's ////////////////////////////

    public static native int[][] getfp(String path, byte[] frameByteArray, int width, int height);
}
