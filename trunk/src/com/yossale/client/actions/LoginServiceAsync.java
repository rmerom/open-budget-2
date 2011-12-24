package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.yossale.client.data.LoginInfo;

public interface LoginServiceAsync {
  public void login(String requestUri, AsyncCallback<LoginInfo> async);
}