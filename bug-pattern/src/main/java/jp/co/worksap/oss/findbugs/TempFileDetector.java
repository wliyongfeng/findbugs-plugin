package jp.co.worksap.oss.findbugs;

import java.util.Arrays;

import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

/**
 * This class detect whether the temporary file created be deleted in a same
 * method.
 *
 * @author Yongfeng LI
 *
 */
public class TempFileDetector extends OpcodeStackDetector {
    private static final int[] LEAVE_METHOD_CODE = { ARETURN, RETURN, LRETURN,
        FRETURN, DRETURN, IRETURN, ATHROW };
    private String methodName = null;
    private boolean isTempFileCreated = false;
    private boolean isCreatedFileDeleted = false;
    BugReporter bugReporter;

    public TempFileDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int seen) {
        if (seen == INVOKESTATIC
                && getClassConstantOperand().endsWith("TemporaryFile")
                && getNameConstantOperand().equals("newTemporaryFile")) {
            isTempFileCreated = true;
        } else if (seen == INVOKEVIRTUAL
                && getClassConstantOperand().endsWith("TemporaryFile")
                && getNameConstantOperand().equals("deleteQuietly")) {
            isCreatedFileDeleted = true;
        }
    }

    @Override
    public void visitMethod(Method obj) {
        visit(obj);
        if (null == methodName) {
            methodName = getMethodName();
        }
        initBeforeMethod();
    }

    @Override
    public void afterOpcode(int code) {
        if (Arrays.asList(LEAVE_METHOD_CODE).contains(code)
                && methodName != null) {
            methodEndDealing();
        }
    }

    private void initBeforeMethod() {
        isTempFileCreated = false;
        isCreatedFileDeleted = false;
    }

    private void methodEndDealing() {
        if (isTempFileCreated && !isCreatedFileDeleted) {
            BugInstance bug = new BugInstance(this, "DELETE_TEMP_FILE",
                    NORMAL_PRIORITY).addClassAndMethod(this).addSourceLine(
                    this, getPC());
            bug.addInt(getPC());
            bugReporter.reportBug(bug);
            methodName = null;
        }
    }

}