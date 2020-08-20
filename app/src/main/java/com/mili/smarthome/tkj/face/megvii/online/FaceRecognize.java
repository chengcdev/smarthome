package com.mili.smarthome.tkj.face.megvii.online;

import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.CharsetUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import mcv.facepass.types.FacePassDetectionResult;
import mcv.facepass.types.FacePassImage;

/**
 * Face++服务器人脸识别请求类
 * 2018-07-20: Created by chenrh.
 */
public class FaceRecognize extends Request<String> {

    private HttpEntity entity;
    private String mGroupName;
    private FacePassDetectionResult mFacePassDetectionResult;
    private Response.Listener<String> mListener;

    public FaceRecognize(String url, String groupName, FacePassDetectionResult detectionResult, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(Method.POST, url, errorListener);
        mGroupName = groupName;
        mFacePassDetectionResult = detectionResult;
        mListener = listener;
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        String parsed;
        try {
            parsed = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
        } catch (UnsupportedEncodingException e) {
            parsed = new String(response.data);
        }
        return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(String response) {
        mListener.onResponse(response);
    }

    @Override
    public String getBodyContentType() {
        return entity.getContentType().getValue();
    }

    @Override
    public byte[] getBody() throws AuthFailureError {
        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//        beginRecogIdArrayList.clear();

        for (FacePassImage passImage : mFacePassDetectionResult.images) {
            /* 将人脸图转成jpg格式图片用来上传 */
            YuvImage img = new YuvImage(passImage.image, ImageFormat.NV21, passImage.width, passImage.height, null);
            Rect rect = new Rect(0, 0, passImage.width, passImage.height);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            img.compressToJpeg(rect, 95, os);
            byte[] tmp = os.toByteArray();
            ByteArrayBody bab = new ByteArrayBody(tmp, String.valueOf(passImage.trackId) + ".jpg");
//            beginRecogIdArrayList.add(passImage.trackId);
            entityBuilder.addPart("image_" + String.valueOf(passImage.trackId), bab);
        }
        StringBody sbody = null;
        try {
            sbody = new StringBody(mGroupName, ContentType.TEXT_PLAIN.withCharset(CharsetUtils.get("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entityBuilder.addPart("group_name", sbody);
        StringBody data = null;
        try {
            data = new StringBody(new String(mFacePassDetectionResult.message), ContentType.TEXT_PLAIN.withCharset(CharsetUtils.get("UTF-8")));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        entityBuilder.addPart("face_data", data);
        entity = entityBuilder.build();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            entity.writeTo(bos);
        } catch (IOException e) {
            VolleyLog.e("IOException writing to ByteArrayOutputStream");
        }
        byte[] result = bos.toByteArray();
        if (bos != null) {
            try {
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }
}
