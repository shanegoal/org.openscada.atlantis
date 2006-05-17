package org.openscada.da.client.net;

import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Set;

public class ItemList extends Observable implements ItemListListener
{
    private Set<String> _items = new HashSet<String>();
    
    public ItemList ()
    {
    }
    
    public void changed ( Collection<String> added, Collection<String> removed, boolean initial )
    {
        int changes = 0;
        
        synchronized ( _items )
        {
            if ( initial )
            {
                _items.clear ();
                _items = new HashSet<String> ( added );
                changes = _items.size ();
            }
            else
            {
                
                for ( String item : added )
                {
                    if ( _items.add(item) )
                        changes++;
                }
                for ( String item : removed )
                {
                    if ( _items.remove(item) )
                        changes++;
                }
            }
        }
        
        // perform notifaction
        if ( changes > 0 )
        {
            setChanged();
            notifyObservers();
        }
        
    }
    
    public Collection<String> getItemList()
    {
        synchronized ( _items )
        {
            return new HashSet<String>(_items);
        }
    }
}
