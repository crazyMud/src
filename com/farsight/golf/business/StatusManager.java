package com.farsight.golf.business;

import java.util.Observable;
import java.util.concurrent.locks.ReentrantLock;

import com.farsight.golf.util.ThreadPoolUtils;

import android.content.Context;
import android.os.Handler;


/*import com.feinno.family.business.notify.AddBuddyNotify;
import com.feinno.family.business.notify.AddBuddySuccessNotify;
import com.feinno.family.business.notify.NewMessageNotify;
import com.feinno.family.sdk.NetworkStackManager;
import com.feinno.family.sdk.NotifyManager;
import com.feinno.family.sdk.protocol.ClientInfoMap;
import com.feinno.family.tools.ThreadPoolUtils;*/

/**
 * @category 登录状态 、心跳管理器
 */
public class StatusManager extends Observable {

    static { // 注册通知
       /* NotifyManager.register(ClientInfoMap.CMD_NTF_ADD_BUDDY,
                AddBuddyNotify.class);
        NotifyManager.register(ClientInfoMap.CMD_NTF_ADD_BUDDY_SUCCESS,
                AddBuddySuccessNotify.class);

        NotifyManager.register(ClientInfoMap.CMD_NTF_NEW_MESSAGE,
                NewMessageNotify.class);*/
    }

    public static boolean isIS_NetWork_OK() {
        return IS_NetWork_OK;
    }

    public static void setIS_NetWork_OK(boolean IS_NetWork_OK) {
        lock.lock();
        StatusManager.IS_NetWork_OK = IS_NetWork_OK;
        lock.unlock();
    }

    private static boolean IS_NetWork_OK = true;

    private static String ssic;

    public static void setSsic(String ssic) {
        StatusManager.ssic = ssic;
    }

    public static String getSsic() {
        return ssic;
    }

    private static ReentrantLock lock = new ReentrantLock();
    private static final int initSocket = 0x002;

    /**
     * @category 私有的构造函数
     */
    private StatusManager() {

    }

    private static StatusManager statusManager = null;

    /**
     * @category 获得当前管理器的实例
     */
    public static StatusManager getInstance() {
        lock.lock();
        if (statusManager == null)
            statusManager = new StatusManager();
        lock.unlock();
        return statusManager;
    }

    private Context context;

    /**
     * @category 获得当前管理器的实例
     */
    public static StatusManager getInstance(Context context) {
        lock.lock();
        if (statusManager == null)
            statusManager = new StatusManager();
        lock.unlock();
        return statusManager;
    }

    /**
     * @category 登陆状态标识
     */
    private static int LOGIN_STATE = -1;

    public static void setLOGIN_STATE(int lOGIN_STATE) {
        LOGIN_STATE = lOGIN_STATE;
    }

 



    /**
     * @category 在非主线程中初始化网络连接
     */
    public void initSocket(final String loginaddress, final Handler handler) {
        ThreadPoolUtils.execute(new Runnable() {
            @Override
            public void run() {
                boolean connectSocket = false;
           /*     NetworkStackManager instance = NetworkStackManager
                        .getInstance();
//				if (!(connectSocket = NetworkStackManager.getInstance()
//						.isSocketConnected())) {
                // 如果网络未连接建立网络连接
                instance.setSocketAddress(loginaddress);


                connectSocket = instance.connectSocket();
				//}
                Message obtain = Message.obtain();
                obtain.obj = connectSocket;
                handler.sendMessage(obtain);*/

            }
        });
    }



}
