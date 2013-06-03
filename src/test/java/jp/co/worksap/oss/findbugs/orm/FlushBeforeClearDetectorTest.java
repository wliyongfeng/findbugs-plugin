package jp.co.worksap.oss.findbugs.orm;

import org.junit.Before;
import org.junit.Test;

import com.youdevise.fbplugins.tdd4fb.DetectorAssert;

import edu.umd.cs.findbugs.BugReporter;

public class FlushBeforeClearDetectorTest {
    private FlushBeforeClearDetector detector;
    private BugReporter bugReporter;

    @Before
    public void setUp() {
        bugReporter = DetectorAssert.bugReporterForTesting();
        detector = new FlushBeforeClearDetector(bugReporter);
    }

    @Test
    public void testNotFlushBeforeClear() throws Exception {
        DetectorAssert.assertBugReported(NotFlushBeforeClearClass.class,
                detector, bugReporter);
    }

    @Test
    public void testFlushBeforeClear() throws Exception {
        DetectorAssert.assertNoBugsReported(DoFlushBeforeClearClass.class,
                detector, bugReporter);
    }

    @Test
    public void testUpdateBeforeClear() throws Exception {
        DetectorAssert.assertBugReported(UpdateEntityManagerBeforeClear.class,
                detector, bugReporter);
    }
}
