
package com.wf;

public class wffrjniID {
    static {
        System.loadLibrary("wffr");
        System.loadLibrary("wffrjniID");

    }

    public wffrjniID() {
    }

    public static native int VerifyLic(String path);

    public static native int initialize(String path, int spoofing);

    public static native int Release();

    public static native int[][] recognize(byte[] frameByteArray, int width, int height);

    public static native int[][] recognizeFromImageFile(String imageFileName);

    public static native int[][] recognizeFromJpegBuffer(byte[] jpegByteArray, int jpegByteArraySize);

    public static native int[][] recognizeDualcam(byte[] frameByteArrayColor, byte[] frameByteArrayIR, int width, int height);	// Recognize from dual camera - color+IR

    public static native float[] confidenceValues();

    public static native int enroll(byte[] frameByteArray, int width, int height);
   
    public static native int enrollFromImageFile(String imageFileName);

    public static native int enrollFromJpegBuffer(byte[] jpegByteArray, int jpegByteArraySize);

    public static native int VerifyImageForEnrollJpegBuffer(byte[] jpegByteArray, int jpegByteSize, int isIRImage);	// verify if the image is suitable for enrollment using jpeg buffer for "wffrjni" Enroll API's

    public static native int GetSpoofingStatus();

    public static native float GetRecognitionThreshold();

    public static native int SetRecognitionThreshold(float threshold);

}
