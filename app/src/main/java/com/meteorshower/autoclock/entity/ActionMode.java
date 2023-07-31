package com.meteorshower.autoclock.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity(nameInDb = "ac_action")
public class ActionMode {

    @Id(autoincrement = false)
    private String ID;
    private int actionCode;
    private String actionName;
    @Generated(hash = 1087232532)
    public ActionMode(String ID, int actionCode, String actionName) {
        this.ID = ID;
        this.actionCode = actionCode;
        this.actionName = actionName;
    }
    @Generated(hash = 1307711111)
    public ActionMode() {
    }
    public String getID() {
        return this.ID;
    }
    public void setID(String ID) {
        this.ID = ID;
    }
    public int getActionCode() {
        return this.actionCode;
    }
    public void setActionCode(int actionCode) {
        this.actionCode = actionCode;
    }
    public String getActionName() {
        return this.actionName;
    }
    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    
    
}
