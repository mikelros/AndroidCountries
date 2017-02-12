package es.ithrek.syncadaptercurrencies.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service needed by the SyncAdapter
 * Created by Mikel on 12/02/17.
 */

public class SyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null)
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }
}
