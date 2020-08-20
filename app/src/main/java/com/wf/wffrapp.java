package com.wf;

import android.graphics.BitmapFactory;

import com.mili.smarthome.tkj.face.FaceInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class wffrapp {

    public static final int IDEL = 0;
    public static final int RECOGNITION = 1;
    public static final int ENROLLMENT = 2;

    public static final int RECOG_THR_HIGH = 65;
    public static final int RECOG_THR_MEDIUM = 58;
    public static final int RECOG_THR_LOW = 50;

    private static final int ASSET_ERROR = 1;
    private static final int RECORD_ERROR = 2;
    private static final int INITIALIZE_ERROR = 3;
    private static final int EXISTS_ERROR = 4;
    private static final int PROCESS_RUNNING_ERROR = 50;
    private static final int UNKNOWN_ERROR = 255;

    private static int spoofing = 0; //活体检测（0-禁用，1-启用）
    private static String assetPath = "";
    private static int frInitialized = 0;
    private static int currentState = IDEL;
    private static int state = IDEL;
    private static List<FaceInfo> faceParseResult = new ArrayList<>();

    private static Semaphore semaphore = new Semaphore(1, false);


    public static void setAssetPath(String path) {
        assetPath = path;
    }

    public static int verifyLicense() {
        if (assetPath == null || assetPath.length() == 0) {
            return ASSET_ERROR;
        }
        return wffrjni.VerifyLic(assetPath);
    }

    public static int getSpoofing() {
        return spoofing;
    }

    public static void setSpoofing(int value) {
        spoofing = value;
    }

    /**
     * @param safeLevel 安全级别：0高，1正常，2普通
     */
    public static void setSafeLevel(int safeLevel) {
        int regcogThr;
        switch (safeLevel) {
            case 0:
                regcogThr = RECOG_THR_HIGH;
                break;
            case 1:
                regcogThr = RECOG_THR_MEDIUM;
                break;
            default:
                regcogThr = RECOG_THR_LOW;
                break;
        }
        wffrjni.SetRecognitionThreshold(regcogThr);
    }

    public static int getSpoofingSensitivity() {
        return wffrjni.GetSpoofingSensitivity();
    }

    public static int setSpoofingSensitivity(int senstivity) {
        return wffrjni.SetSpoofingSensitivity(senstivity);
    }

    public static int getState() {
        return state;
    }

    public static void setState(int value) {
        try {
            semaphore.acquire();
            state = value;
            semaphore.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getVersion() {
        return wffrjni.GetVersionInfo();
    }

    private static void setFaceParseResult(int[][] faceCoordinates, String[] names, float[] confidences) {
        faceParseResult.clear();
        if (faceCoordinates != null) {
            for (int i = 0; i < faceCoordinates.length; i++) {
                int fLeft = faceCoordinates[i][0];
                int fTop = faceCoordinates[i][1];
                int fWidth = faceCoordinates[i][2];
                int fHeight = faceCoordinates[i][3];
                String name = names[i];
                if (name != null && name.contains(" ")) {
                    int index = name.lastIndexOf(' ');
                    name = name.substring(0, index);
                }
                final FaceInfo faceInfo = new FaceInfo()
                        .setFaceId(name)
                        .setSimilar(confidences[i])
                        .setLeft(fLeft)
                        .setTop(fTop)
                        .setRight(fLeft + fWidth)
                        .setBottom(fTop + fHeight);
                faceParseResult.add(faceInfo);
            }
        }
    }

    public static List<FaceInfo> getFaceParseResult() {
        return faceParseResult;
    }

    public static int startExecution(byte[] cameraData, int frameWidth, int frameHeight, String firstName) {
        if (!semaphore.tryAcquire())
            return 0x0A;
        if (assetPath == null || assetPath.length() == 0) {
            semaphore.release();
            return ASSET_ERROR;
        }
        if (currentState == RECOGNITION && state == ENROLLMENT) {
            System.out.println("WFFRJNI: Recognizing already running, stopping the current process and release resources.");
            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;

        }
        if (currentState == ENROLLMENT && state == RECOGNITION) {
            System.out.println("WFFRJNI: Enrolling already running, stopping the current process and release resources.");
            System.out.println("WFFRJNI: Release");
            wffrjni.Release();
            frInitialized = 0;
        }
        if (state == IDEL) {
            if (frInitialized == 1) {
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
            }
        } else if (state == ENROLLMENT) {
            if (frInitialized == 0) {
                int init = wffrjni.initialize(assetPath, frameWidth, frameHeight, frameWidth, 1, spoofing);
                System.out.println("WFFRJNI: Enroll Init, Result=" + init);
                if (init != 0) {
                    semaphore.release();
                    return INITIALIZE_ERROR;
                }
                int addRec = wffrjni.addRecord(firstName, "");
                if (addRec != 0) {
                    System.out.println("WFFRJNI: Adding Record Error: " + addRec);
                    semaphore.release();
                    return RECORD_ERROR;
                }
                frInitialized = 1;
            }
            int[][] faceCoordinates = wffrjni.enroll(cameraData, frameWidth, frameHeight);
            String[] names = wffrjni.nameValues();
            float[] confidences = wffrjni.confidenceValues();
            setFaceParseResult(faceCoordinates, names, confidences);

        } else if (state == RECOGNITION) {
            if (frInitialized == 0) {
                int init = wffrjni.initialize(assetPath, frameWidth, frameHeight, frameWidth, 0, spoofing);
                System.out.println("WFFRJNI: Recognize Init, Result=" + init);
                if (init != 0) {
                    semaphore.release();
                    return INITIALIZE_ERROR;
                }
                frInitialized = 1;
            }
            int[][] faceCoordinates = wffrjni.recognize(cameraData, frameWidth, frameHeight);
            String[] names = wffrjni.nameValues();
            float[] confidences = wffrjni.confidenceValues();
            setFaceParseResult(faceCoordinates, names, confidences);
        }
        currentState = state;
        semaphore.release();
        return 0;
    }

    /**
     * Force stop recognition/enroll process and release engine instance
     **/
    public static int stopExecution() {
        try {
            semaphore.acquire();
            if (assetPath == null || assetPath.length() == 0) {
                semaphore.release();
                return ASSET_ERROR;
            }
            if (frInitialized == 1) {
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
                state = IDEL;
                currentState = IDEL;
            }
            semaphore.release();
            return 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return UNKNOWN_ERROR;
        }
    }

//    public static int getDatabase() {
//        try {
//            semaphore.acquire();
//            if (assetPath == null || assetPath.length() == 0) {
//                semaphore.release();
//                return ASSET_ERROR;
//            }
//            if ((state == IDEL) && (frInitialized == 0)) {
//                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
//                System.out.println("WFFRJNI: Get DB Init, Result=" + init);
//                if (init != 0) {
//                    semaphore.release();
//                    return INITIALIZE_ERROR;
//                }
//                Object[] names = wffrjni.getDbNameList();
//                int[] records;
//                if (names != null) {
//                    records = wffrjni.getDbRecordList(names.length);
//                }
//                wffrjni.Release();
//                state = IDEL;
//                frInitialized = 0;
//                semaphore.release();
//                return 0;
//            } else {
//                semaphore.release();
//                return PROCESS_RUNNING_ERROR;
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return UNKNOWN_ERROR;
//        }
//    }
//
//    public static int deletePerson(int recordID) {
//        try {
//            semaphore.acquire();
//            if (assetPath == null || assetPath.length() == 0) {
//                semaphore.release();
//                return ASSET_ERROR;
//            }
//            if ((state == IDEL) && (frInitialized == 0)) {
//                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
//                System.out.println("WFFRJNI: Delete Person Init, Result=" + init);
//                if (init != 0) {
//                    semaphore.release();
//                    return INITIALIZE_ERROR;
//                }
//                int val = wffrjni.DeletePersonFromDb(recordID);
//                System.out.println("WFFRJNI: Delete Person: recordID=" + recordID + ", Result=" + val);
//                wffrjni.Release();
//                state = IDEL;
//                frInitialized = 0;
//                semaphore.release();
//                return val;
//            } else {
//                semaphore.release();
//                return PROCESS_RUNNING_ERROR;
//            }
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//            return UNKNOWN_ERROR;
//        }
//    }

    public static int deletePersonByName(String name) {
        try {
            semaphore.acquire();
            if (assetPath == null || assetPath.length() == 0) {
                semaphore.release();
                return ASSET_ERROR;
            }
            if (frInitialized == 0) {
                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                System.out.println("WFFRJNI: Delete Person Init, Result=" + init);
                if (init != 0) {
                    semaphore.release();
                    return INITIALIZE_ERROR;
                }
            }
            if (state == IDEL || state == RECOGNITION || state == ENROLLMENT) {
                String firstname = name;
                String lastname = "";
                if (name != null && name.contains(" ")) {
                    lastname = name.substring(name.lastIndexOf(' '));
                    firstname = name.substring(0, name.lastIndexOf(' '));
                }
                int val = wffrjni.DeletePersonByNameFromDb(firstname, lastname);
                System.out.println("WFFRJNI: Delete Person: name=" + name + ", Result=" + val);
                if (frInitialized == 0) {
                    wffrjni.Release();
                }
                semaphore.release();
                return val;
            } else {
                semaphore.release();
                return PROCESS_RUNNING_ERROR;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return UNKNOWN_ERROR;
        }
    }

    public static int deleteDatabase() {
        try {
            semaphore.acquire();
            if (assetPath == null || assetPath.length() == 0) {
                semaphore.release();
                return ASSET_ERROR;
            }
            if ((state == IDEL) && (frInitialized == 0)) {
                int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
                System.out.println("WFFRJNI: Delete DB Init: " + init);
                if (init != 0) {
                    semaphore.release();
                    return INITIALIZE_ERROR;
                }
                int val = wffrjni.DeleteDatabase();
                System.out.println("WFFRJNI: Delete DB, Result=" + val);
                wffrjni.Release();
                state = IDEL;
                frInitialized = 0;
                semaphore.release();
                return val;
            } else {
                semaphore.release();
                return PROCESS_RUNNING_ERROR;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return UNKNOWN_ERROR;
        }
    }

    public static int runEnrollFromJpegFile(String imageFileName, String name) {
        try {
            semaphore.acquire();
            if (assetPath == null || assetPath.length() == 0) {
                semaphore.release();
                return ASSET_ERROR;
            }
            if (currentState > 0 && frInitialized == 1) {
                System.out.println("WFFRJNI: Video mode already running, stopping the current process and release resources.");
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
            }
            int init = wffrjni.initialize(assetPath, 0, 0, 0, 1, 0);
            System.out.println("WFFRJNI: Enroll Init, Result=" + init);
            if (init != 0) {
                semaphore.release();
                return INITIALIZE_ERROR;
            }
            frInitialized = 1;
            String lastName = "";
            if (name != null && name.contains(" ")) {
                lastName = name.substring(name.lastIndexOf(' '));
                name = name.substring(0, name.lastIndexOf(' '));

            }
            int addRec = wffrjni.addRecord(name, lastName);
            if (addRec != 0) {
                System.out.println("WFFRJNI: Adding Record Error: " + addRec);
                semaphore.release();
                return RECORD_ERROR;
            }
            int[][] faceCoordinates = wffrjni.enrollFromImageFile(imageFileName);
//            String[] names = wffrjni.nameValues();
//            float[] confidences = wffrjni.confidenceValues();
//            // 获取图片的宽和高
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(imageFileName, options);
//            setFaceParseResult(faceCoordinates, names, confidences);

            wffrjni.Release();
            frInitialized = 0;
            semaphore.release();
            if (faceCoordinates != null && faceCoordinates.length > 0) {// && confidences[0] == 0) {
                return 0;
            } else {
                return EXISTS_ERROR;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            return UNKNOWN_ERROR;
        }
    }

    public static int runRecognizeFromJpegFile(String imageFileName) {
        try {
            semaphore.acquire();
            if (assetPath == null || assetPath.length() == 0) {
                semaphore.release();
                return ASSET_ERROR;
            }
            if (currentState > 0 && frInitialized == 1) {
                System.out.println("WFFRJNI: Video mode already running, stopping the current process and release resources.");
                System.out.println("WFFRJNI: Release");
                wffrjni.Release();
                frInitialized = 0;
                currentState = IDEL;
                state = IDEL;
            }
            int init = wffrjni.initialize(assetPath, 0, 0, 0, 0, 0);
            System.out.println("WFFRJNI: Recognize Init, Result=" + init);
            if (init != 0) {
                semaphore.release();
                return INITIALIZE_ERROR;
            }
            int[][] faceCoordinates = wffrjni.recognizeFromImageFile(imageFileName);
            String[] names = wffrjni.nameValues();
            float[] confidences = wffrjni.confidenceValues();
            // 获取图片的宽和高
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFileName, options);
            //
            setFaceParseResult(faceCoordinates, names, confidences);

            wffrjni.Release();
            semaphore.release();
            return 0;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return UNKNOWN_ERROR;
        }
    }

}
