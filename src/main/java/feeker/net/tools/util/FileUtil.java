package feeker.net.tools.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 文件读写工具类
 * Created by LGZ on 2016/8/22.
 */
public class FileUtil {

    /**
     * 将字符串写入到文件中
     *
     * @param filePath 文件路径
     * @param str      字符串
     * @throws IOException
     */
    public static void writeToFile(String filePath, String str) throws IOException {

        File file = new File(filePath);
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) fileParent.mkdirs();
        file.createNewFile();

        FileWriter fw = new FileWriter(filePath);
        fw.write(str);
        fw.close();
    }
}
