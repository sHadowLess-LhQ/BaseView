package cn.com.shadowless.baseview.callback;

import androidx.annotation.NonNull;

/**
 * The interface Init data call back.
 *
 * @param <T> the type parameter
 * @author sHadowLess
 */
public interface InitDataCallBack<T> {
    /**
     * 成功且带数据
     *
     * @param t the t
     */
    void initSuccessViewWithData(@NonNull T t);

    /**
     * 成功不带数据
     */
    void initSuccessViewWithOutData();

    /**
     * 失败
     *
     * @param e the e
     */
    void initFailView(Throwable e);
}
