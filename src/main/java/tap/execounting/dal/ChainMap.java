package tap.execounting.dal;

import java.util.HashMap;
import java.util.Map;

/**
 * NamedQuery parameter helper to create the querys
 * 
 * @author karesti
 */
public class ChainMap
{

    private Map<String, Object> parameters = null;

    private ChainMap(String name, Object value)
    {
        this.parameters = new HashMap<String, Object>();
        this.parameters.put(name, value);
    }

    public static ChainMap with(String name, Object value)
    {
        return new ChainMap(name, value);
    }

    public ChainMap n(String name, Object value)
    {
        this.parameters.put(name, value);
        return this;
    }

    public Map<String, Object> yo()
    {
        return this.parameters;
    }
}
