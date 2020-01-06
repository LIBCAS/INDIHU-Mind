package core.detach.store;

import core.detach.objects.Object1;
import core.detach.objects.QObject1;
import core.store.DatedStore;

public class Object1Store extends DatedStore<Object1, QObject1> {

    public Object1Store() {
        super(Object1.class, QObject1.class);
    }
}
