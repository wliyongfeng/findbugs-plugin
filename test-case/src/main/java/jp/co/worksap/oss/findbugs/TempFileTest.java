package jp.co.worksap.oss.findbugs;

import java.io.IOException;

import com.google.common.io.Files;

import jp.co.worksap.oss.findbugs.tools.TemporaryFile;

public class TempFileTest {

    public void test() throws IOException {
        TemporaryFile tempFile = TemporaryFile.newTemporaryFile();
        try {
            Files.touch(tempFile);
        } finally {
            tempFile.deleteQuietly();
        }
    }

}
