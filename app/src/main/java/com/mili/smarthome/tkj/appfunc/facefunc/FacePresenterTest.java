package com.mili.smarthome.tkj.appfunc.facefunc;

import com.mili.smarthome.tkj.utils.LogUtils;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class FacePresenterTest {

    private static ExecutorService mExecutor;

    public static void enroll(String photoDir) {
        File dir = new File(photoDir);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        if (mExecutor == null) {
            mExecutor = Executors.newSingleThreadExecutor();
        }
        mExecutor.execute(new EnrollTask(dir));
    }

    private static class EnrollTask implements Runnable {

        private File mPhotoDir;

        public EnrollTask(File photoDir) {
            mPhotoDir = photoDir;
        }

        @Override
        public void run() {
            int count = 0;
            int suc = 0;
            int fail = 0;
            File[] photoFiles = mPhotoDir.listFiles();
            for (File photoFile : photoFiles) {
                String cardNo = BaseFacePresenter.cardIdToString(++count);
                String faceId = BaseFacePresenter.genResidentFaceId(cardNo);
                boolean result = FacePresenterProxy.enrollFromImage(photoFile.getAbsolutePath(), faceId);
                if (result) {
                    suc++;
                    photoFile.delete();
                } else {
                    fail++;
                }
            }
            LogUtils.d("-- FACE ENROLL: suc %d, fail %d", suc, fail);
        }
    }
}
