package tap.execounting.dal;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Startup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Demo Data Loader
 * 
 * @author karesti
 * @version 1.0
 */
public class DataModule
{
    @SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(DataModule.class);

    private final CrudServiceDAO dao;

    public DataModule(CrudServiceDAO dao)
    {
        super();
        this.dao = dao;
    }

    @Startup
    public void initialize()
    {
        /*List<User> users = new ArrayList<User>();

        users.add(new User("Christophe Cordenier", "cordenier", "ccordenier@example.com",
                "cordenier"));

        LOGGER.info("-- Loading initial demo data");
        try{
        create(users);
        }
        catch(Exception ex){
        	LOGGER.info("Exception while creatin user");
        }

        List<User> userList = dao.findWithNamedQuery(User.ALL);
        LOGGER.info("Users " + userList);
        LOGGER.info("-- Data Loaded. Exit");*/
    }

    @SuppressWarnings("unused")
	private void create(List<?> entities)
    {
        for (Object e : entities)
        {
            dao.create(e);
        }
    }

}
