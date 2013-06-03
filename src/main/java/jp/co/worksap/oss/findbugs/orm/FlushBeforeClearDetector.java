package jp.co.worksap.oss.findbugs.orm;

import edu.umd.cs.findbugs.BugInstance;
import edu.umd.cs.findbugs.BugReporter;
import edu.umd.cs.findbugs.bcel.OpcodeStackDetector;

public class FlushBeforeClearDetector extends OpcodeStackDetector {
    private BugReporter bugReporter;
    private boolean haveFlush;

    public FlushBeforeClearDetector(BugReporter bugReporter) {
        this.bugReporter = bugReporter;
        haveFlush = false;
    }

    @Override
    public void sawOpcode(int seen) {
        if (seen == INVOKEINTERFACE
                && getDottedClassConstantOperand().equals(
                        "javax.persistence.EntityManager")) {
            checkFlush();
            checkUpdate();
            checkClear();
        }
        if (isReturn(seen)) {
            haveFlush = false;
        }
    }

    private void checkClear() {
        if (getNameConstantOperand().equals("clear")) {
            if (!haveFlush) {
                BugInstance bug = new BugInstance(this,
                        "FLUSH_ENTITY_MANAGER_BEFORE_CLEAR", NORMAL_PRIORITY)
                        .addClassAndMethod(this).addSourceLine(this, getPC());
                bugReporter.reportBug(bug);
            }
        }
    }

    private void checkFlush() {
        if (getNameConstantOperand().equals("flush")) {
            haveFlush = true;
        }
    }

    private void checkUpdate() {
        if (!getNameConstantOperand().endsWith("flush")
                && !getNameConstantOperand().equals("clear") && haveFlush) {
            haveFlush = false;
        }
    }
}
