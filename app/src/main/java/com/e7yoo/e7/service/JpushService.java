package com.e7yoo.e7.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.sql.DbThreadPool;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * helper methods.
 */
public class JpushService extends IntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_SAVE_PUSH_MSG = "com.e7yoo.e7.service.action.SAVE_PUSH_MSG";
//    private static final String ACTION_BAZ = "com.e7yoo.e7.service.action.BAZ";

    private static final String EXTRA_PUSH_MSG = "com.e7yoo.e7.service.extra.PUSH_MSG";
    private static final String EXTRA_HAS_EXTRAS = "com.e7yoo.e7.service.extra.HAS_EXTRAS";

    public JpushService() {
        super("JpushService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionSavePushMsg(Context context, PushMsg pushMsg, boolean hashExtras) {
        Intent intent = new Intent(context, JpushService.class);
        intent.setAction(ACTION_SAVE_PUSH_MSG);
        intent.putExtra(EXTRA_PUSH_MSG, pushMsg);
        intent.putExtra(EXTRA_HAS_EXTRAS, hashExtras);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
//    public static void startActionBaz(Context context, String param1, String param2) {
//        Intent intent = new Intent(context, JpushService.class);
//        intent.setAction(ACTION_BAZ);
//        intent.putExtra(EXTRA_PARAM1, param1);
//        intent.putExtra(EXTRA_PARAM2, param2);
//        context.startService(intent);
//    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SAVE_PUSH_MSG.equals(action)) {
                final PushMsg param1 = (PushMsg) intent.getSerializableExtra(EXTRA_PUSH_MSG);
                final boolean param2 = intent.getBooleanExtra(EXTRA_HAS_EXTRAS, false);
                handleActionPushMsg(param1, param2);
            } /*else if (ACTION_BAZ.equals(action)) {
                final String param1 = intent.getStringExtra(EXTRA_PARAM1);
                final String param2 = intent.getStringExtra(EXTRA_PARAM2);
                handleActionBaz(param1, param2);
            }*/
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionPushMsg(PushMsg pushMsg, boolean hashExtras) {

        DbThreadPool.getInstance().insertPushMsg(pushMsg, hashExtras);
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
//    private void handleActionBaz(String param1, String param2) {
//        // TODO: Handle action Baz
//        throw new UnsupportedOperationException("Not yet implemented");
//    }
}
