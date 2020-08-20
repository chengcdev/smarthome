/*
 * Copyright (C) 2010 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mili.widget.zxing.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;

import com.mili.widget.zxing.decode.Decoder;

@SuppressWarnings("deprecation") // camera APIs
final class PreviewCallback implements Camera.PreviewCallback {

    private static final String TAG = PreviewCallback.class.getSimpleName();

    private final CameraConfigurationManager mConfigManager;
    private Decoder mDecoder;

    PreviewCallback(CameraConfigurationManager configManager) {
        this.mConfigManager = configManager;
    }

    void setDecoder(Decoder decoder) {
        this.mDecoder = decoder;
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        Point cameraResolution = mConfigManager.getCameraResolution();
        if (cameraResolution != null && mDecoder != null) {
            mDecoder.decode(data, cameraResolution.x, cameraResolution.y);
            mDecoder = null;
        } else {
            Log.d(TAG, "Got preview callback, but no handler or resolution available");
        }
    }

}
