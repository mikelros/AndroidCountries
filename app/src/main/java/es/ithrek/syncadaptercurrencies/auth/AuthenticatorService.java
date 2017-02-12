package es.ithrek.syncadaptercurrencies.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Authenticator service for the authenticator
 * Created by Mikel on 12/02/17.
 */

public class AuthenticatorService extends Service {
    private Authenticator authenticator;

    @Override
    public void onCreate() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
