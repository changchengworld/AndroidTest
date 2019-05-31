package wenba.com.androidtest.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import wenba.com.androidtest.dao.daoentity.BookDBEntity;
import wenba.com.androidtest.dao.daoentity.UserDBEntity;

import wenba.com.androidtest.dao.BookDBEntityDao;
import wenba.com.androidtest.dao.UserDBEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig bookDBEntityDaoConfig;
    private final DaoConfig userDBEntityDaoConfig;

    private final BookDBEntityDao bookDBEntityDao;
    private final UserDBEntityDao userDBEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        bookDBEntityDaoConfig = daoConfigMap.get(BookDBEntityDao.class).clone();
        bookDBEntityDaoConfig.initIdentityScope(type);

        userDBEntityDaoConfig = daoConfigMap.get(UserDBEntityDao.class).clone();
        userDBEntityDaoConfig.initIdentityScope(type);

        bookDBEntityDao = new BookDBEntityDao(bookDBEntityDaoConfig, this);
        userDBEntityDao = new UserDBEntityDao(userDBEntityDaoConfig, this);

        registerDao(BookDBEntity.class, bookDBEntityDao);
        registerDao(UserDBEntity.class, userDBEntityDao);
    }
    
    public void clear() {
        bookDBEntityDaoConfig.clearIdentityScope();
        userDBEntityDaoConfig.clearIdentityScope();
    }

    public BookDBEntityDao getBookDBEntityDao() {
        return bookDBEntityDao;
    }

    public UserDBEntityDao getUserDBEntityDao() {
        return userDBEntityDao;
    }

}