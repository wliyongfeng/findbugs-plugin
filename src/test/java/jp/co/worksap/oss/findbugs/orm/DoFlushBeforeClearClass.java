package jp.co.worksap.oss.findbugs.orm;

import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

public class DoFlushBeforeClearClass {

    public void flushBoforeClear() {
        EntityManager manager = mock(EntityManager.class);
        manager.flush();
        manager.clear();
    }
}
