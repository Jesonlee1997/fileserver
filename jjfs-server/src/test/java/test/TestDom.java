package test;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import java.io.File;

/**
 * Created by JesonLee
 * on 2017/5/31.
 */
public class TestDom {
    @Test
    public void test1() {

        String classPath = TestDom.class.getResource("/").getPath();
        File file = new File(classPath + "/jjfs.xml");

        Document document = null;
        SAXReader reader = new SAXReader();
        try {
            document = reader.read(file);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Element root = document.getRootElement();
        Element view = root.element("contentRoot");
        Element controller = root.element("filePort");
        Element urlPatterns = root.element("httpPort");

        System.out.println(view.getText());
        System.out.println(controller.getText());
        System.out.println(urlPatterns.getText());
    }
}
