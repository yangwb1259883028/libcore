package cn.linhome.library.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.sunday.eventbus.SDBaseEvent;
import com.sunday.eventbus.SDEventManager;
import com.sunday.eventbus.SDEventObserver;

import cn.linhome.lib.holder.objects.FObjectsHolder;
import cn.linhome.lib.holder.objects.FStrongObjectsHolder;
import cn.linhome.lib.holder.objects.ForeachCallback;
import cn.linhome.library.R;
import cn.linhome.library.common.SDFragmentManager;
import qiu.niorgai.StatusBarCompat;


public abstract class SDBaseActivity extends AppCompatActivity implements
        SDEventObserver,
        OnClickListener
{
    private SDFragmentManager mFragmentManager;

    private ProgressDialog mProgressDialog;

    private FObjectsHolder<ActivityLifecycleCallback> mLifecycleCallbackHolder;
    private FObjectsHolder<ActivityResultCallback> mActivityResultCallbackHolder;
    private FObjectsHolder<ActivityTouchEventCallback> mTouchEventCallbackHolder;
    private FObjectsHolder<ActivityKeyEventCallback> mKeyEventCallbackHolder;

    public Activity getActivity()
    {
        return this;
    }

    public final FObjectsHolder<ActivityLifecycleCallback> getLifecycleCallbackHolder()
    {
        if (mLifecycleCallbackHolder == null)
        {
            mLifecycleCallbackHolder = new FStrongObjectsHolder<>();
        }
        return mLifecycleCallbackHolder;
    }

    public final FObjectsHolder<ActivityResultCallback> getActivityResultCallbackHolder()
    {
        if (mActivityResultCallbackHolder == null)
        {
            mActivityResultCallbackHolder = new FStrongObjectsHolder<>();
        }
        return mActivityResultCallbackHolder;
    }

    public final FObjectsHolder<ActivityTouchEventCallback> getTouchEventCallbackHolder()
    {
        if (mTouchEventCallbackHolder == null)
        {
            mTouchEventCallbackHolder = new FStrongObjectsHolder<>();
        }
        return mTouchEventCallbackHolder;
    }

    public final FObjectsHolder<ActivityKeyEventCallback> getKeyEventCallbackHolder()
    {
        if (mKeyEventCallbackHolder == null)
        {
            mKeyEventCallbackHolder = new FStrongObjectsHolder<>();
        }
        return mKeyEventCallbackHolder;
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        SDEventManager.register(this);

        final int layoutId = onCreateContentView();
        if (layoutId != 0)
        {
            setContentView(layoutId);
        }
        init(savedInstanceState);

        setStatusBar();

        notifyOnCreate(savedInstanceState);
    }

    /**
     * 沉浸式设置
     */
    protected void setStatusBar()
    {
        StatusBarCompat.setStatusBarColor(this, getResources().getColor(R.color.res_main_color));
    }

    /**
     * 返回布局activity布局id，基类调用的顺序：onCreateContentView()-setContentView()-init()
     *
     * @return
     */
    protected int onCreateContentView()
    {
        return 0;
    }

    /**
     * 重写此方法初始化
     *
     * @param savedInstanceState
     */
    protected abstract void init(Bundle savedInstanceState);

    @Override
    public void setContentView(int layoutId)
    {
        View contentView = getLayoutInflater().inflate(layoutId, (ViewGroup) findViewById(android.R.id.content), false);
        setContentView(contentView);
    }

    @Override
    public void setContentView(View view)
    {
        View contentView = addTitleViewIfNeed(view);
        contentView.setFitsSystemWindows(true);
        super.setContentView(contentView);

        onInitContentView(contentView);
    }

    /**
     * setContentView方法之后会回调此方法，可以用来初始化View
     *
     * @param view
     */
    protected void onInitContentView(View view)
    {

    }

    /**
     * 返回标题栏布局id
     *
     * @return
     */
    protected int onCreateTitleViewResId()
    {
        return 0;
    }

    /**
     * 为contentView添加titleView
     *
     * @param contentView
     * @return
     */
    private View addTitleViewIfNeed(View contentView)
    {
        View viewFinal = contentView;

        int resId = onCreateTitleViewResId();
        if (resId != 0)
        {
            View titleView = getLayoutInflater().inflate(resId, (ViewGroup) findViewById(android.R.id.content), false);

            LinearLayout linAll = new LinearLayout(this);
            linAll.setOrientation(LinearLayout.VERTICAL);
            linAll.addView(titleView);
            linAll.addView(contentView);
            viewFinal = linAll;

            onInitTitleView(titleView);
        }
        return viewFinal;
    }

    /**
     * 初始化标题栏view
     *
     * @param view
     */
    protected void onInitTitleView(View view)
    {

    }

    @Override
    protected void onStart()
    {
        super.onStart();
        notifyOnStart();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        notifyOnResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        notifyOnPause();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        notifyOnStop();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        SDEventManager.unregister(this);
        dismissProgressDialog();
        notifyOnDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        try
        {
            super.onSaveInstanceState(outState);
            if (outState != null)
            {
                outState.remove("android:support:fragments");
            }
            notifyOnSaveInstanceState(outState);
        } catch (Exception e)
        {
            onSaveInstanceStateException(e);
        }
    }

    protected void onSaveInstanceStateException(Exception e)
    {

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        try
        {
            super.onRestoreInstanceState(savedInstanceState);
            notifyOnRestoreInstanceState(savedInstanceState);
        } catch (Exception e)
        {
            onRestoreInstanceStateException(e);
        }
    }

    protected void onRestoreInstanceStateException(Exception e)
    {
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        notifyOnActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev)
    {
        final Object data = getTouchEventCallbackHolder().foreachReverse(new ForeachCallback<ActivityTouchEventCallback>()
        {
            @Override
            protected void next(ActivityTouchEventCallback item)
            {
                if (item.dispatchTouchEvent(SDBaseActivity.this, ev))
                {
                    setData(true);
                    breakForeach();
                }
            }
        });
        if (data != null)
        {
            // 不为null的话直接返回true，不做data为true的判断，上面已经写死了
            return true;
        }

        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean dispatchKeyEvent(final KeyEvent event)
    {
        final Object data = getKeyEventCallbackHolder().foreachReverse(new ForeachCallback<ActivityKeyEventCallback>()
        {
            @Override
            protected void next(ActivityKeyEventCallback item)
            {
                if (item.dispatchKeyEvent(SDBaseActivity.this, event))
                {
                    setData(true);
                    breakForeach();
                }
            }
        });
        if (data != null)
        {
            // 不为null的话直接返回true，不做data为true的判断，上面已经写死了
            return true;
        }

        return super.dispatchKeyEvent(event);
    }

    /**
     * activity是否处于竖屏方向
     *
     * @return
     */
    public boolean isOrientationPortrait()
    {
        return Configuration.ORIENTATION_PORTRAIT == getResources().getConfiguration().orientation;
    }

    /**
     * activity是否处于横屏方向
     *
     * @return
     */
    public boolean isOrientationLandscape()
    {
        return Configuration.ORIENTATION_LANDSCAPE == getResources().getConfiguration().orientation;
    }

    /**
     * 设置activity为竖屏
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public void setOrientationPortrait()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    /**
     * 设置activity为横屏
     */
    @SuppressLint("SourceLockedOrientationActivity")
    public void setOrientationLandscape()
    {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    public Dialog showProgressDialog(String msg)
    {
        if (mProgressDialog == null)
        {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage(msg);
        mProgressDialog.show();
        return mProgressDialog;
    }

    public void dismissProgressDialog()
    {
        if (mProgressDialog != null)
        {
            try
            {
                mProgressDialog.dismiss();
            } catch (Exception e)
            {
            }
        }
    }

    /**
     * 设置activity是否全屏
     *
     * @param fullScreen true-全屏，false-不全屏
     */
    public void setFullScreen(boolean fullScreen)
    {
        if (fullScreen)
        {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else
        {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    //------------notify callback start------------------

    private void notifyOnCreate(final Bundle savedInstanceState)
    {
        getLifecycleCallbackHolder().foreach(new ForeachCallback<ActivityLifecycleCallback>()
        {
            @Override
            protected void next(ActivityLifecycleCallback item)
            {
                item.onActivityCreated(SDBaseActivity.this, savedInstanceState);
            }
        });
    }

    private void notifyOnStart()
    {
        getLifecycleCallbackHolder().foreach(new ForeachCallback<ActivityLifecycleCallback>()
        {
            @Override
            protected void next(ActivityLifecycleCallback item)
            {
                item.onActivityStarted(SDBaseActivity.this);
            }
        });
    }

    private void notifyOnResume()
    {
        getLifecycleCallbackHolder().foreach(new ForeachCallback<ActivityLifecycleCallback>()
        {
            @Override
            protected void next(ActivityLifecycleCallback item)
            {
                item.onActivityResumed(SDBaseActivity.this);
            }
        });
    }

    private void notifyOnPause()
    {
        getLifecycleCallbackHolder().foreach(new ForeachCallback<ActivityLifecycleCallback>()
        {
            @Override
            protected void next(ActivityLifecycleCallback item)
            {
                item.onActivityPaused(SDBaseActivity.this);
            }
        });
    }

    private void notifyOnStop()
    {
        getLifecycleCallbackHolder().foreach(new ForeachCallback<ActivityLifecycleCallback>()
        {
            @Override
            protected void next(ActivityLifecycleCallback item)
            {
                item.onActivityStopped(SDBaseActivity.this);
            }
        });
    }

    private void notifyOnDestroy()
    {
        getLifecycleCallbackHolder().foreach(new ForeachCallback<ActivityLifecycleCallback>()
        {
            @Override
            protected void next(ActivityLifecycleCallback item)
            {
                item.onActivityDestroyed(SDBaseActivity.this);
            }
        });
    }

    private void notifyOnSaveInstanceState(final Bundle outState)
    {
        getLifecycleCallbackHolder().foreach(new ForeachCallback<ActivityLifecycleCallback>()
        {
            @Override
            protected void next(ActivityLifecycleCallback item)
            {
                item.onActivitySaveInstanceState(SDBaseActivity.this, outState);
            }
        });
    }

    private void notifyOnRestoreInstanceState(final Bundle savedInstanceState)
    {
        getLifecycleCallbackHolder().foreach(new ForeachCallback<ActivityLifecycleCallback>()
        {
            @Override
            protected void next(ActivityLifecycleCallback item)
            {
                item.onActivityRestoreInstanceState(SDBaseActivity.this, savedInstanceState);
            }
        });
    }

    private void notifyOnActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        getActivityResultCallbackHolder().foreach(new ForeachCallback<ActivityResultCallback>()
        {
            @Override
            protected void next(ActivityResultCallback item)
            {
                item.onActivityResult(SDBaseActivity.this, requestCode, resultCode, data);
            }
        });
    }

    //------------notify callback end------------------

    @Override
    public void addContentView(View view, ViewGroup.LayoutParams params)
    {
        if (params == null)
        {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
        super.addContentView(view, params);
    }

    @Override
    public void onEventMainThread(SDBaseEvent event)
    {

    }

    @Override
    public void onClick(View v)
    {

    }

    /**
     * 不再维护，直接调用原生的方法操作
     *
     * @return
     */
    @Deprecated
    public SDFragmentManager getSDFragmentManager()
    {
        if (mFragmentManager == null)
        {
            mFragmentManager = new SDFragmentManager(getSupportFragmentManager());
        }
        return mFragmentManager;
    }

    public interface ActivityLifecycleCallback extends Application.ActivityLifecycleCallbacks
    {
        void onActivityRestoreInstanceState(Activity activity, Bundle savedInstanceState);
    }

    public interface ActivityResultCallback
    {
        void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);
    }

    public interface ActivityTouchEventCallback
    {
        boolean dispatchTouchEvent(Activity activity, MotionEvent ev);
    }

    public interface ActivityKeyEventCallback
    {
        boolean dispatchKeyEvent(Activity activity, KeyEvent event);
    }
}
