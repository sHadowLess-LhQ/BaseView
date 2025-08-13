package cn.com.shadowless.baseview.event;

import androidx.viewbinding.ViewBinding;

import cn.com.shadowless.baseview.manager.VmObjManager;

public interface UpdateObjEvent {
    void update(VmObjManager<? extends ViewBinding> manager);
}
