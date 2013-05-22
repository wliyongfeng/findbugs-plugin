package jp.co.worksap.oss.findbugs;

import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;
public class CheckDeleteTempFileDetectorTest {

    @Test
    public void delteTempFile() throws Exception {
        BugReporter bugReporter = DetectorAssert.bugReporterForTesting();
        TempFileDetector detector = new TempFileDetector(bugReporter);
        // Next assert that your detector has raised a bug against a specific
        // class
        DetectorAssert.assertBugReported(TempFileTest.class, detector,
                bugReporter);
    }
}
