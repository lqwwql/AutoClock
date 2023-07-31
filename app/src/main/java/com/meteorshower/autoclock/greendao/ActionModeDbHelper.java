package com.meteorshower.autoclock.greendao;

import com.meteorshower.autoclock.entity.ActionMode;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

public class ActionModeDbHelper {

    private DaoSession daoSession;

    private ActionModeDbHelper() {
        daoSession = GreenDaoManager.getInstance().getDaoSession();
    }

    private static class ActionSingleInstance {
        public static ActionModeDbHelper INSTANCE;
    }

    public static ActionModeDbHelper getInstance() {
        if (ActionSingleInstance.INSTANCE == null) {
            ActionSingleInstance.INSTANCE = new ActionModeDbHelper();
        }
        return ActionSingleInstance.INSTANCE;
    }

    public List<ActionMode> getAllAction() {
        List<ActionMode> result = daoSession.getActionModeDao().queryBuilder().list();
        if (result == null) {
            result = new ArrayList<>();
        }
        return result;
    }

    public ActionMode getActionItem(String ID) {
        QueryBuilder<ActionMode> queryBuilder = daoSession.getActionModeDao().queryBuilder();
        return queryBuilder.where(ActionModeDao.Properties.ID.eq(ID)).unique();
    }

    public void insert(ActionMode actionMode) {
        daoSession.getActionModeDao().insert(actionMode);
    }

    public void insertList(List<ActionMode> actionModes){
        daoSession.getActionModeDao().insertInTx(actionModes);
    }

    public void update(ActionMode actionMode) {
        daoSession.getActionModeDao().update(actionMode);
    }

    public void delete(ActionMode actionMode) {
        daoSession.getActionModeDao().delete(actionMode);
    }

    public void deleteAll() {
        daoSession.getActionModeDao().deleteAll();
    }

}
