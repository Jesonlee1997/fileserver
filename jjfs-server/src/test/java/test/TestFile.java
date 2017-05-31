package test;

import org.junit.Test;

import java.io.File;
import java.net.URL;

/**
 * Created by JesonLee
 * on 2017/5/18.
 */
public class TestFile {

    private String fileName = "J:\\Java\\projects\\fileserver\\src\\main\\java\\client\\test.txt";

    @Test
    public void test1() {
        File file = new File(fileName);
        System.out.println(file.length());
        System.out.println(file.getName());
        System.out.println(file.getPath());
    }

    @Test
    public void test2() {
        String str1 = "my name is Jesonlee, i have two egg";
        String str2 = "Jesonlee";
        String str3 = str1.substring(str1.lastIndexOf(str2) + str2.length());
        System.out.println(str3);
    }

    @Test
    public void test3() {
        URL url = TestFile.class.getResource("/");
        System.out.println(url.getFile());
        File file = new File(url.getFile());
        String configPath = file.getParent() + "/conf/jjfs.xml";
        System.out.println(configPath);
    }
}
