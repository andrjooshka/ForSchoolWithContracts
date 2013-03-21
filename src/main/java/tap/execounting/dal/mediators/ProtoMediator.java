package tap.execounting.dal.mediators;

import org.apache.tapestry5.ioc.annotations.Inject;
import tap.execounting.dal.CRUDServiceDAO;

import java.util.List;

/**
 * User: truth0
 * Date: 3/17/13
 * Time: 10:07 PM
 */
public class ProtoMediator<T> {

    protected Class<T> clazz;

    @Inject
    protected CRUDServiceDAO dao;

    protected T unit;

    public T getUnit(){
        return unit;
    }

    public T getUnitById(long id){
        return dao.find(clazz, id);
    }

    protected List<T> cache;

    public List<T> getGroup(){
        return cache;
    }

    protected boolean cacheIsNull(){
        return cache == null;
    }

    protected boolean cacheIsEmpty(){
        return cacheIsNull() || cache.size() == 0;
    }
}
