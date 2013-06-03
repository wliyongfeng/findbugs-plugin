package jp.co.worksap.oss.findbugs.orm;

import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;
public class UpdateEntityManagerBeforeClear {

    public void updateBeforeClear() {
        EntityManager manager = mock(EntityManager.class);
        manager.flush();
        Object entity = mock(Object.class);
        manager.remove(entity);
        manager.clear();
    }
}
