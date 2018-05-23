package com.example.dx.utilproject.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 *
 * @author dx
 *         <p>
 *         BaseActivity是项目中所有的Activity的基类
 *         将常用的一些方法,简单封装了一下
 *         <p>
 *         注意:
 *         如果要考虑兼容Android3.0之前的版本也使用Fragment
 *         可以考虑让BaseActivity继承自FragmentActivity
 *         然后使用getSupportFragmentManager()方法获取FragmentManager
 */
public abstract class BaseActivity extends Activity {
    private static final String TAG = "BaseActivity";
    private static LinkedList<Activity> activityList = new LinkedList<>();
    private static Activity visibleActivity;
    private Dialog progressDialog;
    private Fragment currentFragment;

    /**
     * 子类可以不用重写onCreate方法了，
     * 注意子类这句setContentView(R.layout.activity_main);代码需要移动到
     * initView()方法中
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activity设置为无标题栏的状态
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        activityList.addLast(this);

        /*
        下面个方法的调用顺序不能改变，否则可能会对其他Activity的逻辑造成影响
        如果需要新增Activity,注意这三个方法的执行顺序.
         */
        initView(savedInstanceState);
        initData();
        initListener();
    }

    /**
     * 初始化view，该方法中必须要有 setContentView(R.layout.activity_main);
     * 在 {@link #initData()} 方法和 {@link #initListener()} 方法之前执行
     *
     * @param savedInstanceState 就是onCreate方法参数中的savedInstanceState
     *                           方便恢复Activity后，恢复之前的数据
     */
    public abstract void initView(@Nullable Bundle savedInstanceState);

    /**
     * 初始化数据,在 {@link #initView(Bundle)} 方法之后执行
     * 在 {@link #initListener()} 方法之前执行
     */
    public abstract void initData();

    /**
     * 初始化各种监听器,在 {@link #initView(Bundle)} 方法和 {@link #initData()} 方法之后执行
     */
    public abstract void initListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityList.remove(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        visibleActivity = this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        visibleActivity = null;
    }

    /**
     * 获取当前可见的Activity，如果当前没有可见的Activity，就返回null
     * 判定规则是，只要Activity调用了onPause方法，就不可见了
     *
     * @return 当前可见的Activity, 如果当前没有可见的Activity, 就返回null
     */
    public static Activity getVisibleActivity() {
        return visibleActivity;
    }

    public static Activity getTopActivity() {
        if (activityList.size() > 0) {
            return activityList.getLast();
        } else {
            return null;
        }
    }

    public static Class<? extends Activity> getTopActivityClass() {
        if (activityList.size() > 0) {
            return activityList.getLast().getClass();
        } else {
            return null;
        }
    }

    public void startActivity(Class<?> targetActivityCls) {
        startActivity(targetActivityCls, null);
    }

    public void startActivity(Class<?> targetActivityCls, Bundle bundle) {
        Intent intent = new Intent(this, targetActivityCls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);

        //使用动画效果,被启动界面会从左到右滑动过来
        overridePendingTransition(android.R.anim.slide_in_left, 0);
    }

    /**
     * 杀死指定的Activity,例如:killActivity(MainActivity.class);
     *
     * @param targetActivityCls 目标界面
     */
    public void killActivity(Class<?> targetActivityCls) {
        int targetActivityIndex = -1;
        for (int i = 0; i < activityList.size(); i++) {
            Activity activity = activityList.get(i);
            if (activity.getClass().getName().equals(targetActivityCls.getName())) {
                targetActivityIndex = i;
                break;
            }
        }
        if (targetActivityIndex >= 0) {
            activityList.get(targetActivityIndex).finish();
            activityList.remove(targetActivityIndex);
            Log.d(TAG, "成功杀死：" + targetActivityCls.getName());
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void showToastLong(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }

    public String getResourcesString(int rid) {
        return getResources().getString(rid);
    }

    protected static void finishAll() {
        for (int i = 0; i < activityList.size(); i++) {
            activityList.get(i).finish();
        }
        activityList.clear();
    }

    public void closeInputMethod() {
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    public void showProgressDialog(String msg) {
        //经过了测试，ProgressDialog可以复用
        if (progressDialog == null) {
            progressDialog = createProgressDialog(msg);
        }
        progressDialog.show();
    }
    private Dialog createProgressDialog(String msg){
        ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setMessage(msg);
        progressDialog.setCancelable(false);
        progressDialog.show();
        return progressDialog;
    }

    public void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void finish() {
        super.finish();

        //使用默认动画
        closeInputMethod();
        overridePendingTransition(0, android.R.anim.slide_out_right);
    }

    /**
     * Fragment替换(当前destrory,新的create)
     */
    public void fragmentReplace(int target, Fragment toFragment, boolean backStack) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        String toClassName = toFragment.getClass().getSimpleName();
        if (manager.findFragmentByTag(toClassName) == null) {
            transaction.replace(target, toFragment, toClassName);
            if (backStack) {
                transaction.addToBackStack(toClassName);
            }
            transaction.commit();
        }
    }

    /**
     * Fragment替换(核心为隐藏当前的,显示现在的,用过的将不会destrory与create)
     */
    public void smartFragmentReplace(int target, Fragment toFragment) {
        FragmentManager manager = getFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();

        // 如有当前在使用的->隐藏当前的
        if (currentFragment != null) {
            transaction.hide(currentFragment);
        }
        String toClassName = toFragment.getClass().getSimpleName();

        // toFragment之前添加使用过->显示出来
        if (manager.findFragmentByTag(toClassName) != null) {
            transaction.show(toFragment);
        } else {
            // toFragment还没添加使用过->添加上去
            transaction.add(target, toFragment, toClassName);
        }
        transaction.commit();

        // toFragment更新为当前的
        currentFragment = toFragment;
    }
}
