package com.example.dx.utilproject.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.util.Stack;

/**
 * 如果要考虑兼容Android3.0之前的版本也使用Fragment
 * 可以考虑让BaseActivity继承自FragmentActivity
 * 然后使用getSupportFragmentManager()方法获取FragmentManager
 */
public abstract class BaseActivity extends Activity {
    private static Stack<Activity> activityStack=new Stack<>();
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
        //Activity设置为无标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //将Activity推入栈中,进行记录
        activityStack.push(this);

        /*
        下面个方法的调用顺序不能改变
         */
        //初始化布局View
        initView(savedInstanceState);
        //初始化数据
        initData();
        //初始化各种Listener
        initListener();
    }

    /**
     * 初始化view，该方法中必须要有 setContentView(R.layout.activity_main);
     * @param savedInstanceState 就是onCreate方法参数中的savedInstanceState
     *                           方便恢复Activity后，恢复之前的数据
     */
    public abstract void initView(@Nullable Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    public abstract void initData();
    /**
     * 初始化各种监听器
     */
    public abstract void initListener();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(activityStack.contains(this)){
            activityStack.remove(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        visibleActivity =this;
    }

    @Override
    protected void onPause() {
        super.onPause();
        visibleActivity =null;
    }

    /**
     * 获取当前可见的Activity，如果当前没有可见的Activity，就返回null
     * 判定规则是，只要Activity调用了onPause方法，就不可见了
     * @return
     */
    public static Activity getVisibleActivity(){
        return visibleActivity;
    }

    /**
     * 获取最顶部的Activity
     * @return
     */
    public static Activity getTopActivity(){
        if(!activityStack.empty()) {
            return activityStack.peek();
        }else {
            return null;
        }
    }

    public void startActivity(Class<?> targetActivityCls){
        startActivity(targetActivityCls,null);
    }
    public void startActivity(Class<?> targetActivityCls, Bundle bundle){
        Intent intent=new Intent(this,targetActivityCls);
        if (bundle!=null) {
            intent.putExtras(bundle);
        }

        startActivity(intent);
        //使用动画效果
        overridePendingTransition(android.R.anim.slide_in_left, 0);
    }
    public void showToast(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }
    public void showToastLong(String msg){
        Toast.makeText(this,msg, Toast.LENGTH_LONG).show();
    }
    public String getResourcesString(int rid){
        return getResources().getString(rid);
    }
    protected static void finishAll(){
        while (!activityStack.empty()) {
            Activity activity=activityStack.pop();
            activity.finish();
        }
    }
    public void closeInputMethod(){
        View view=getWindow().peekDecorView();
        if (view!=null){
            InputMethodManager inputMethodManager= (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    public void showProgressDialog(String msg){
        //经过了测试，ProgressDialog可以复用
        if (progressDialog==null){
            progressDialog=createProgressDialog(msg);
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
    public void dismissProgressDialog(){
        if (progressDialog!=null){
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
     * */
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
        } else {// toFragment还没添加使用过->添加上去
            transaction.add(target, toFragment, toClassName);
        }
        transaction.commit();
        // toFragment更新为当前的
        currentFragment = toFragment;
    }
}
