package jp.co.worksap.oss.findbugs;

import org.apache.bcel.classfile.Method;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public class CheckDeleteTempFile extends OpcodeStackDetector {
    private String methodName = null;
    private boolean isTempFileCreated = false;
    private boolean isCreatedFileDeleted = false;
    BugReporter bugReporter;

    public CheckDeleteTempFile(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
    }

    @Override
    public void sawOpcode(int seen) {
        if (!visitingMethod()) {
            if (methodName != null) {
                methodEndDealing();
                methodName = null;
            }
        } else {
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
    }

    @Override
    public void visitMethod(Method obj) {
        visit(obj);
        if (null == methodName) {
            methodName = getMethodName();
        } else if (!methodName.equals(getMethodName())) {
            methodEndDealing();
            methodName = getMethodName();
        }
        initBeforeMethod();
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
