package jp.co.worksap.oss.findbugs.orm;

import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;
public class NotFlushBeforeClearClass {

    public void notFlushBeforeClear() {
        EntityManager manager = mock(EntityManager.class);
        manager.clear();
    }
}
