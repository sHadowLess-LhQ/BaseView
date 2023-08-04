package cn.com.shadowless.baseview.callback;

import java.util.List;

/**
 * The interface Permission call back.
 *
 * @author sHadowLess
 */
public interface PermissionCallBack {
    /**
     * Agree.
     */
    void agree();

    /**
     * Disagree.
     *
     * @param name the name
     */
    void disagree(List<String> name);

    /**
     * Ban.
     *
     * @param name the name
     */
    void ban(List<String> name);
}
