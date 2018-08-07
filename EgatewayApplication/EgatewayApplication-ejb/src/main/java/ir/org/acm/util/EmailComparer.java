package ir.org.acm.util;

import ir.org.acm.entity.Email;
import java.util.Comparator;

public class EmailComparer implements Comparator<Email> {

    @Override
    public int compare(Email o1, Email o2) {
        if (o1.getRecieveDate() == null || o2.getRecieveDate() == null) {
            return -1;
        }
        if (o1.getRecieveDate() == null && o2.getRecieveDate() == null) {
            return -1;
        }
        return o2.getRecieveDate().compareTo(o1.getRecieveDate());
    }

}
