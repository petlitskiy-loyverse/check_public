package com.loyverse.dashboard;

import com.loyverse.dashboard.base.mvp.LoginPresenter;
import com.loyverse.dashboard.base.mvp.LoginView;
import com.loyverse.dashboard.core.DataModel;
import com.loyverse.dashboard.core.Server;
import com.loyverse.dashboard.mvp.presenters.LoginPresenterImpl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class PresenterTest {

    @Mock
    DataModel dataModelMock;


    @Mock
    Server serverMock;

    @Mock
    LoginView loginViewMock;

    //todo integration test for presenter.login() method
    @Test
    public void testLogin() {
        given(dataModelMock.isUserLoggedIn()).willReturn(false).willReturn(true);
        LoginPresenter<LoginView> presenter = new LoginPresenterImpl(dataModelMock, serverMock);
        presenter.bind(loginViewMock);

        presenter.handleIfAlreadyLoggedIn();
        verify(loginViewMock, never()).login();
        presenter.handleIfAlreadyLoggedIn();
        verify(loginViewMock).login();

        presenter.unbind(loginViewMock);
    }

}
