package core.detach.store;


import core.detach.objects.Object2;
import core.detach.objects.QObject2;
import core.store.DatedStore;

public class Object2Store extends DatedStore<Object2, QObject2> {

    public Object2Store() {
        super(Object2.class, QObject2.class);
    }
}
