package server1;

import org.junit.Test;

import java.io.File;

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
}
