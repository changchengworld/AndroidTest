package wenba.com.androidtest.dao.daoentity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by silvercc on 18/1/9.
 */

@Entity
public class BookDBEntity {
    @Id(autoincrement = true)
    private Long _id;
    private String name;

    @Generated(hash = 918991590)
    public BookDBEntity(Long _id, String name) {
        this._id = _id;
        this.name = name;
    }

    @Generated(hash = 810933876)
    public BookDBEntity() {
    }

    public Long get_id() {
        return _id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "id = " + _id + "; name = " + name;
    }
}
