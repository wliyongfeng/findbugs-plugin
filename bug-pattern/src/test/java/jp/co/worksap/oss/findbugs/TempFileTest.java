package jp.co.worksap.oss.findbugs;

import java.io.IOException;

import com.google.common.io.Files;

public class TempFileTest {

    public void test() throws IOException {
        TemporaryFile tempFile = TemporaryFile.newTemporaryFile();
        Files.touch(tempFile);
    }

}
