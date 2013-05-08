package jp.co.worksap.oss.findbugs;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public class CheckDeleteTempFile extends OpcodeStackDetector {
    private boolean isTempFileCreated = false;
    private boolean isTempFileDeleted = false;
    BugReporter bugReporter;

    public CheckDeleteTempFile(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int seen) {
        if (seen != INVOKESTATIC) {
            return;
        }
        if (!getClassConstantOperand().endsWith("TemporaryFile")) {
            return;
        }
        if (getNameConstantOperand().equals("newTemporaryFile")) {
            isTempFileCreated = true;
        }
        if (!isTempFileCreated) {
            return;
        }
        BugInstance bug = new BugInstance(this, "DELETE_TEMP_FILE",
                NORMAL_PRIORITY).addClassAndMethod(this).addSourceLine(
                this, getPC());
        bug.addInt(getPC());
        bugReporter.reportBug(bug);
    }

}
