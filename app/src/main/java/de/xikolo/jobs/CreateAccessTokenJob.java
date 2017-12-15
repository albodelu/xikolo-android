package de.xikolo.jobs;

import android.util.Log;

import com.birbit.android.jobqueue.Params;

import de.xikolo.config.Config;
import de.xikolo.jobs.base.BaseJob;
import de.xikolo.jobs.base.JobCallback;
import de.xikolo.models.AccessToken;
import de.xikolo.network.ApiService;
import de.xikolo.storages.UserStorage;
import de.xikolo.utils.NetworkUtil;
import retrofit2.Response;

public class CreateAccessTokenJob extends BaseJob {

    public static final String TAG = CreateAccessTokenJob.class.getSimpleName();

    private String email;
    private String password;

    public CreateAccessTokenJob(JobCallback callback, String email, String password) {
        super(new Params(PRIORITY_HIGH), callback);
        this.callback = callback;
        this.email = email;
        this.password = password;
    }

    @Override
    public void onAdded() {
        if (Config.DEBUG) Log.i(TAG, TAG + " added | email " + email);
    }

    @Override
    public void onRun() throws Throwable {
        if (NetworkUtil.isOnline()) {

            Response<AccessToken> response =
                    ApiService.getInstance().createToken(email, password).execute();

            AccessToken token = response.body();
            if (response.isSuccessful() && token != null) {
                if (Config.DEBUG) Log.i(TAG, "AccessToken created");

                UserStorage userStorage = new UserStorage();
                userStorage.saveAccessToken(token.token);
                userStorage.saveUserId(token.userId);

                callback.success();
            } else {
                if (Config.DEBUG) Log.w(TAG, "AccessToken not created");
                callback.error(JobCallback.ErrorCode.ERROR);
            }
        } else {
            callback.error(JobCallback.ErrorCode.NO_NETWORK);
        }
    }

}