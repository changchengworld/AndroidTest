package wenba.com.androidtest.dao.daoentity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by silvercc on 18/1/9.
 */

@Entity
public class UserDBEntity {
    @Id(autoincrement = true)
    private Long _id;
    private String name;
    private int gender;

    @Generated(hash = 1369783917)
    public UserDBEntity(Long _id, String name, int gender) {
        this._id = _id;
        this.name = name;
        this.gender = gender;
    }

    @Generated(hash = 919333459)
    public UserDBEntity() {
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

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    @Override
    public String toString() {
        return "id = " + _id + "; name = " + name + "; gender = " + gender;
    }

}
