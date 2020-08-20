package com.mili.smarthome.tkj.face.megvii.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.renderscript.Type;

import com.mili.smarthome.tkj.app.Const;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class NV21ToBitmap {
    private RenderScript rs;
    private ScriptIntrinsicYuvToRGB yuvToRgbIntrinsic;
    private Type.Builder yuvType, rgbaType;
    private Allocation in, out;

    public NV21ToBitmap(Context context)
    {
        rs = RenderScript.create(context);
        yuvToRgbIntrinsic = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs));
    }

    private void saveYuvToJpg(Bitmap bitmap, String fileName)
    {
        String path = Const.Directory.ROOT + "/" + fileName + ".jpg";
        File file = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Bitmap nv21ToBitmap(byte[] nv21, int width, int height)
    {
        if (yuvType == null){
            yuvType = new Type.Builder(rs, Element.U8(rs)).setX(nv21.length);
            in = Allocation.createTyped(rs, yuvType.create(), Allocation.USAGE_SCRIPT);
            rgbaType = new Type.Builder(rs, Element.RGBA_8888(rs)).setX(width).setY(height);
            out = Allocation.createTyped(rs, rgbaType.create(), Allocation.USAGE_SCRIPT);
        }
        in.copyFrom(nv21);
        yuvToRgbIntrinsic.setInput(in);
        yuvToRgbIntrinsic.forEach(out);
        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        out.copyTo(bmpout);
//        saveYuvToJpg(bmpout, "frame");
        return bmpout;
    }

    public Bitmap nv21ToBitmap1(byte[] nv21, int width, int height)
    {
        int frameSize = width * height;
        int[] rgba = new int[frameSize];

        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                int y = (0xff & ((int) nv21[i * width + j]));
                int u = (0xff & ((int) nv21[frameSize + (i >> 1) * width + (j & ~1) + 0]));
                int v = (0xff & ((int) nv21[frameSize + (i >> 1) * width + (j & ~1) + 1]));
                y = y < 16 ? 16 : y;

                int r = Math.round(1.164f * (y - 16) + 1.596f * (v - 128));
                int g = Math.round(1.164f * (y - 16) - 0.813f * (v - 128) - 0.391f * (u - 128));
                int b = Math.round(1.164f * (y - 16) + 2.018f * (u - 128));

                r = r < 0 ? 0 : (r > 255 ? 255 : r);
                g = g < 0 ? 0 : (g > 255 ? 255 : g);
                b = b < 0 ? 0 : (b > 255 ? 255 : b);

                rgba[i * width + j] = 0xff000000 + (b << 16) + (g << 8) + r;
            }

        Bitmap bmpout = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bmpout.setPixels(rgba, 0 , width, 0, 0, width, height);
//        saveYuvToJpg(bmpout, "frame");
        return bmpout;
    }

    public Bitmap nv21ToBitmap2(byte[] nv21, int width, int height)
    {
        YuvImage yuvimage = new YuvImage(nv21, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        yuvimage.compressToJpeg(new Rect(0, 0, width, height), 80, baos);
        byte[] jdata = baos.toByteArray();
        BitmapFactory.Options bitmapFatoryOptions = new BitmapFactory.Options();
        bitmapFatoryOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bmpout = BitmapFactory.decodeByteArray(jdata, 0, jdata.length, bitmapFatoryOptions);
//        saveYuvToJpg(bmpout, "frame");
        return bmpout;
    }

    public Bitmap nv21ToBitmap3(byte[] nv21, int width, int height)
    {
        int[] colors = decodeYuvToRgb1(nv21, width, height);
        if(colors == null){
            return null;
        }
        Bitmap bmpout = Bitmap.createBitmap(colors, 0, width, width, height,
                Bitmap.Config.RGB_565);
        try {
            byte[] imgBytes = null;
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            if (bmpout.compress(Bitmap.CompressFormat.JPEG, 100, outStream)){
                imgBytes = outStream.toByteArray();
                    // 创建指定路径的文件
                String path = Const.Directory.ROOT + "/frame4.jpg";
                File file = new File(path);
                // 如果文件存在则删除
                if (file.exists()) {
                    file.delete();
                }
                // 创建新的空文件
                file.createNewFile();
                // 获取文件的输出流对象
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(imgBytes);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bmpout;
    }

    private int[] decodeYuvToRgb(byte[] yuv420sp, int width, int height)
    {
        final int frameSize = width * height;
        int rgb[] = new int[frameSize];
        for (int j = 0, yp = 0; j < height; j++) {
            int uvp = frameSize + (j >> 1) * width, u = 0, v = 0;
            for (int i = 0; i < width; i++, yp++) {
                int y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }
                int y1192 = 1192 * y;
                int r = (y1192 + 1634 * v);
                int g = (y1192 - 833 * v - 400 * u);
                int b = (y1192 + 2066 * u);
                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

//                rgba[yp] = 0xff000000 | ((b << 6) & 0xff0000)
//                        | ((g >> 2) & 0xff00) | ((r >> 10) & 0xff);
//				下面为百度到的方法，其实就是r和b变量调换下位置
				rgb[yp] = 0xff000000 | ((r << 6) & 0xff0000)
						| ((g >> 2) & 0xff00) | ((b >> 10) & 0xff);
            }
        }
        return rgb;
    }

    private int[] decodeYuvToRgb1(byte[] yuv420sp, int width, int height) {
        final int frameSize = width * height;
        int rgb[] = new int[frameSize];
        int i, j, index = 0, rgb_index = 0;
        int y, u, v;
        int r, g, b, nv_index = 0;

        for (i = 0; i < height; i++) {
            for (j = 0; j < width; j++) {
                //nv_index = (rgb_index / 2 - width / 2 * ((i + 1) / 2)) * 2;
                nv_index = i / 2 * width + j - j % 2;

                y = yuv420sp[rgb_index];
                u = yuv420sp[frameSize + nv_index];
                v = yuv420sp[frameSize + nv_index + 1];


                r = y + (140 * (v - 128)) / 100;  //r
                g = y - (34 * (u - 128)) / 100 - (71 * (v - 128)) / 100; //g
                b = y + (177 * (u - 128)) / 100; //b

                if (r > 255) r = 255;
                if (g > 255) g = 255;
                if (b > 255) b = 255;
                if (r < 0) r = 0;
                if (g < 0) g = 0;
                if (b < 0) b = 0;

                index = rgb_index % width + (height - i - 1) * width;
                rgb[index * 3 + 0] = b;
                rgb[index * 3 + 1] = g;
                rgb[index * 3 + 2] = r;
                rgb_index++;
            }
        }
        return rgb;
    }

    private int convertYUVtoARGB(int y, int u, int v) {
        int r,g,b;

        r = y + (int)1.402f*u;
        g = y - (int)(0.344f*v +0.714f*u);
        b = y + (int)1.772f*v;
        r = r>255? 255 : r<0 ? 0 : r;
        g = g>255? 255 : g<0 ? 0 : g;
        b = b>255? 255 : b<0 ? 0 : b;
        return 0xff000000 | (r<<16) | (g<<8) | b;
    }

}
