package edu.uci.isr.archstudio4.comp.booleannotation;



import edu.uci.isr.xarchflat.ObjRef;
import edu.uci.isr.xarchflat.XArchFlatInterface;

public interface IBooleanGuard
{
    public ObjRef toXArch( ObjRef context, XArchFlatInterface xarch );

    public String toString();
}
