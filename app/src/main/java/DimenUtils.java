import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 屏幕适配工具类
 */
public class DimenUtils {

    /**
     * 资源文件根目录
     */
    private static final String ROOT_DIR = "./app/src/main/res/";
    /**
     * dimen资源模版
     * <p>param1: 单位</p>
     * <p>param2: 分子</p>
     * <p>param3: 实际尺寸</p>
     */
    private static final String DIMEN_TEMPLATE = "<dimen name=\"%1$s_%2$s\">%3$.1f%1$s</dimen>";

    private static final int DENOMINATOR = 360; // 分母(360等分)
    private static final List<DimenInfo> mDimenList;

    static {
        mDimenList = new ArrayList<>();
        // sp
        mDimenList.add(DimenInfo.createSp(8));
        mDimenList.add(DimenInfo.createSp(9));
        mDimenList.add(DimenInfo.createSp(10));
        mDimenList.add(DimenInfo.createSp(11));
        mDimenList.add(DimenInfo.createSp(12));
        mDimenList.add(DimenInfo.createSp(13));
        mDimenList.add(DimenInfo.createSp(14));
        mDimenList.add(DimenInfo.createSp(15));
        mDimenList.add(DimenInfo.createSp(16));
        mDimenList.add(DimenInfo.createSp(18));
        mDimenList.add(DimenInfo.createSp(20));
        mDimenList.add(DimenInfo.createSp(22));
        mDimenList.add(DimenInfo.createSp(24));
        mDimenList.add(DimenInfo.createSp(28));
        mDimenList.add(DimenInfo.createSp(32));
        mDimenList.add(DimenInfo.createSp(36));
        mDimenList.add(DimenInfo.createSp(40));
        mDimenList.add(DimenInfo.createSp(48));
        // dp
        mDimenList.add(DimenInfo.createDp(0.5f, "0_5"));
    }

    public static void main(String[] args) {
        build(360);
        build(600);
        build(768);
    }

    /**
     * 生成dimens资源
     * @param swdp 最小宽度(dp)
     */
    private static void build(int swdp) {
        StringBuilder resBuffer = new StringBuilder();
        resBuffer.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>").append('\n');
        resBuffer.append("<resources>").append('\n');

        final BigDecimal denominator = new BigDecimal(DENOMINATOR);
        final BigDecimal fraction = new BigDecimal(swdp).divide(denominator, 4, RoundingMode.HALF_UP);
        // 创建枚举尺寸资源
        for (DimenInfo dimenInfo : mDimenList) {
            BigDecimal val = new BigDecimal(dimenInfo.value).multiply(fraction).setScale(1, RoundingMode.HALF_UP);
            resBuffer.append('\t').append(String.format(Locale.getDefault(), DIMEN_TEMPLATE, dimenInfo.unit, dimenInfo.key, val.floatValue())).append('\n');
        }
        // 创建基础尺寸资源
        for (int i = 0; i <= 640; i++) {
            BigDecimal val = new BigDecimal(i).multiply(fraction).setScale(1, RoundingMode.HALF_UP);
            resBuffer.append('\t').append(String.format(Locale.getDefault(), DIMEN_TEMPLATE, "dp", Integer.toString(i), val.floatValue())).append('\n');
        }
        resBuffer.append("</resources>");

        File resFile = createFile(swdp);
        if (resFile == null)
            return;
        write(resFile, resBuffer.toString());
    }

    private static File createFile(int swdp) {
        try {
            File dir = new File(ROOT_DIR, String.format(Locale.getDefault(), "values-sw%1$ddp", swdp));
            if (!dir.exists() || !dir.isDirectory()) {
                dir.mkdirs();
            }
            File file = new File(dir, "dimens.xml");
            file.createNewFile();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void write(File file, String data) {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            bw.write(data);
            bw.flush();
            bw.close();
            fos.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static class DimenInfo {
        private float value;
        private String unit;
        private String key;

        private DimenInfo(int value, String unit) {
            this.value = value;
            this.unit = unit;
            this.key = Integer.toString(value);
        }

        private DimenInfo(float value, String unit, String key) {
            this.value = value;
            this.unit = unit;
            this.key = key;
        }

        public static DimenInfo createSp(int value) {
            return new DimenInfo(value, "sp");
        }

        public static DimenInfo createDp(int value) {
            return new DimenInfo(value, "dp");
        }

        public static DimenInfo createDp(float value, String key) {
            return new DimenInfo(value, "dp", key);
        }
    }
}
