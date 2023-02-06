package com.loyverse.dashboard.base.mvp;

import com.loyverse.dashboard.base.LogoutEvent;
import com.loyverse.dashboard.base.PermissionEvent;
import com.loyverse.dashboard.base.server.ServerError;
import com.loyverse.dashboard.base.server.ServerResult;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;

import org.greenrobot.eventbus.EventBus;

import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public abstract class BasePresenter<T extends BaseView> {
    protected T view;
    protected DataModel dataModel;
    protected Server server;
    protected CompositeSubscription subscriptions = new CompositeSubscription();
    protected Action1<Throwable> onError = throwable -> {
        Timber.e( throwable );
        if (throwable instanceof ServerError) {
            ServerError error = (ServerError) throwable;
            if (error.getResult().equals( ServerResult.ACCESS_DENIED.result )) {
                EventBus.getDefault().postSticky( new PermissionEvent().setType( PermissionEvent.ACCESS_DENIED ) );
            } else if (error.getResult().equals( ServerResult.BLOCKED_BY_BILLING.result )) {
                EventBus.getDefault().postSticky( new PermissionEvent().setType( PermissionEvent.BLOCKED_BY_BILLING ) );
            } else if (error.getResult().equals( ServerResult.BAD_COOKIE_AUTH.result )) {
                LogoutEvent logoutEvent = new LogoutEvent();
                logoutEvent.setForce( true );
                EventBus.getDefault().postSticky( logoutEvent );
            }
        }
        view.hideLoadingDialog();
        view.showToastOnException( throwable );

    };

    protected BasePresenter(DataModel dataModel, Server server) {
        this.dataModel = dataModel;
        this.server = server;

    }

    public void bind(T view) {
        this.view = view;
    }

    public void unbind(T view) {
        subscriptions.clear();//unsubscribe() is not the behavior we want, would need to create a new CompositeSubscription in bind()
        if(this.view == view) {
            this.view = null;
        }
    }

    protected void runIfNotNull(T o, RunnableWithParam<T> handler) {
        if(o != null){
            handler.run(o);
        }
    }

    protected interface RunnableWithParam <T> {
        void run(T o);
    }
}
