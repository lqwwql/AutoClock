package com.meteorshower.autoclock.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.meteorshower.autoclock.entity.ActionMode;

import com.meteorshower.autoclock.greendao.ActionModeDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig actionModeDaoConfig;

    private final ActionModeDao actionModeDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        actionModeDaoConfig = daoConfigMap.get(ActionModeDao.class).clone();
        actionModeDaoConfig.initIdentityScope(type);

        actionModeDao = new ActionModeDao(actionModeDaoConfig, this);

        registerDao(ActionMode.class, actionModeDao);
    }
    
    public void clear() {
        actionModeDaoConfig.clearIdentityScope();
    }

    public ActionModeDao getActionModeDao() {
        return actionModeDao;
    }

}
