package com.e7yoo.e7.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.TextUtils;

import com.e7yoo.e7.E7App;
import com.e7yoo.e7.R;
import com.e7yoo.e7.model.PrivateMsg;
import com.e7yoo.e7.model.PrivateMsg.Type;
import com.e7yoo.e7.model.PushMsg;
import com.e7yoo.e7.model.Robot;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessageDbHelper extends SQLiteOpenHelper {
    @SuppressWarnings("unused")
    private static final String TAG = "MessageDbHelper";
    private static final String DB_NAME = "db_e7yoo_info";
    private static final int DB_VERSION = 5;
    private static final String TABLE_MESSAGE = "t_message_info";
    private static final String TABLE_FAVORITE = "t_favorite_info";
    private static final String TABLE_ROBOT = "t_robot";
    private static final String TABLE_PUSH_MSG = "t_pushmsg";

    private static MessageDbHelper mInstance = null;
    private SQLiteDatabase mDatabase;

    //double check
    public static MessageDbHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (MessageDbHelper.class) {
                if (mInstance == null) {
                    mInstance = new MessageDbHelper(context);
                }
            }
        }
        return mInstance;
    }

    private MessageDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mDatabase = getWritableDatabase();
    }

    StringBuilder sb_message = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ")
            .append(TABLE_MESSAGE).append("(")
            .append(MessageInfoColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
            //.append(MessageInfoColumns.USER).append(" TEXT NOT NULL,")
            .append(MessageInfoColumns.CODE).append(" INTEGER,")
            .append(MessageInfoColumns.TIME).append(" INTEGER,")
            .append(MessageInfoColumns.CONTENT).append(" TEXT,")
            .append(MessageInfoColumns.TYPE).append(" TEXT,")
            .append(MessageInfoColumns.URL).append(" TEXT,")
            .append(MessageInfoColumns.ID).append(" TEXT,")
            .append(MessageInfoColumns.TIME2).append(" TEXT,")
            .append(MessageInfoColumns.TITLE).append(" TEXT,")
            .append(MessageInfoColumns.NEWS_TYPE).append(" TEXT,")
            .append(MessageInfoColumns.ROBOT_ID).append(" INTEGER")
            .append(")");
    StringBuilder sql_favorite = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ")
            .append(TABLE_FAVORITE).append("(")
            .append(FavoriteInfoColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append(FavoriteInfoColumns.USER).append(" TEXT NOT NULL,")
            .append(FavoriteInfoColumns.CODE).append(" INTEGER,")
            .append(FavoriteInfoColumns.NEWS_TYPE).append(" TEXT")
            .append(")");
    StringBuilder sql_robot = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ")
            .append(TABLE_ROBOT).append("(")
            .append(RobotColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append(RobotColumns.NAME).append(" TEXT NOT NULL,")
            .append(RobotColumns.ICON).append(" TEXT,")
            .append(RobotColumns.TIME).append(" INTEGER,")
            .append(RobotColumns.BIRTHTIME).append(" INTEGER,")
            .append(RobotColumns.WELCOME).append(" TEXT,")
            .append(RobotColumns.SEX).append(" INTEGER,")
            .append(RobotColumns.VOICE).append(" INTEGER,")
            .append(RobotColumns.BG).append(" TEXT,")
            .append(RobotColumns.BGBLUR).append(" INTEGER,")
            .append(RobotColumns.SCORE).append(" INTEGER,")
            .append(RobotColumns.DESC).append(" TEXT")
            .append(")");
    StringBuilder sql_push_msg = new StringBuilder().append("CREATE TABLE IF NOT EXISTS ")
            .append(TABLE_PUSH_MSG).append("(")
            .append(PushMsgColumns._ID).append(" INTEGER PRIMARY KEY AUTOINCREMENT,")
            .append(PushMsgColumns.TIME).append(" INTEGER,")
            .append(PushMsgColumns.ACTION).append(" INTEGER,")
            .append(PushMsgColumns.URL).append(" TEXT,")
            .append(PushMsgColumns.PIC_URL).append(" TEXT,")
            .append(PushMsgColumns.MSG_TIME).append(" TEXT,")
            .append(PushMsgColumns.CONTENT_URL).append(" TEXT,")
            .append(PushMsgColumns.CONTENT_URL_HINT).append(" TEXT,")
            .append(PushMsgColumns.CONTENT_PIC_URL).append(" TEXT,")
            .append(PushMsgColumns.TITLE).append(" TEXT,")
            .append(PushMsgColumns.CONTENT).append(" TEXT,")
            .append(PushMsgColumns.EXTRAS).append(" TEXT,")
            .append(PushMsgColumns.MSG_ID).append(" TEXT,")
            .append(PushMsgColumns.DESC).append(" TEXT,")
            .append(PushMsgColumns.UNREAD).append(" INTEGER")
            .append(")");

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sb_message.toString());
        db.execSQL(sql_favorite.toString());
        db.execSQL(sql_robot.toString());
        db.execSQL(sql_push_msg.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            if (oldVersion == 1) {
                // 数据库版本1基础上增加robot表，message表增加robot_id列，其他数据库未改变
                db.execSQL(sql_robot.toString());
                db.execSQL(sql_push_msg.toString());
                db.execSQL("ALTER TABLE "+ TABLE_MESSAGE + " ADD " + MessageInfoColumns.ROBOT_ID + " INTEGER;");
            } else {
                // 其他情况，将表销毁再创建（后续版本可自行修改）
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROBOT);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUSH_MSG);
                onCreate(db);
            }
        }
    }

    private static class MessageInfoColumns implements BaseColumns {
        private static final String _ID = "_id";
        //private static final String USER = "user";
        private static final String CODE = "code";
        private static final String TIME = "time";
        private static final String CONTENT = "content";
        private static final String TYPE = "type";
        private static final String URL = "url";
        private static final String ID = "id";
        private static final String TIME2 = "time2";
        private static final String TITLE = "title";
        private static final String NEWS_TYPE = "news_type";
        private static final String ROBOT_ID = "robot_id";

    }

    private static class FavoriteInfoColumns implements BaseColumns {
        private static final String _ID = "_id";
        private static final String USER = "user";
        private static final String CODE = "code";
        private static final String NEWS_TYPE = "news_type";

    }

    private static class RobotColumns implements BaseColumns {
        private static final String _ID = "_id";
        private static final String NAME = "name";
        private static final String ICON = "icon";
        private static final String TIME = "time";
        private static final String BIRTHTIME = "birthTime";
        private static final String WELCOME = "welcome";
        private static final String SEX = "sex";
        private static final String VOICE = "voice";
        private static final String BG = "bg";
        private static final String BGBLUR = "bgblur";
        private static final String SCORE = "score";
        private static final String DESC = "desc"; // 备用

    }

    private static class PushMsgColumns implements BaseColumns {
        private static final String _ID = "_id";
        private static final String TIME = "time";
        private static final String ACTION = "action";
        private static final String URL = "url";
        private static final String PIC_URL = "pic_url";
        private static final String MSG_TIME = "msg_time";
        private static final String CONTENT_PIC_URL = "content_pic_url";
        private static final String CONTENT_URL = "content_url";
        private static final String CONTENT_URL_HINT = "content_url_hint";
        private static final String TITLE = "title";
        private static final String CONTENT = "content";
        private static final String EXTRAS = "extras";
        private static final String MSG_ID = "msg_id";
        private static final String DESC = "desc";
        private static final String UNREAD = "unread";

    }

    public ArrayList<PrivateMsg> getPrivateMsgs(int robot_id, int id, int num) {
        if (num <= 0) {
            num = 100;
        }
        String where = null;
        if (id > 0) {
            where = MessageInfoColumns._ID + "<" + id;
        }
        String whereName;
        /*if (E7App.mApp.getString(R.string.mengmeng).equals(name)) {
            whereName = MessageInfoColumns.USER + " = ? OR " + MessageInfoColumns.USER + " is null";
        } else {
            whereName = MessageInfoColumns.USER + " = ?";
        }*/
        if(robot_id <= 0) {
            whereName = MessageInfoColumns.ROBOT_ID + " = ? OR " + MessageInfoColumns.ROBOT_ID + " is null";
        } else {
            whereName = MessageInfoColumns.ROBOT_ID + " = ?";
        }
        if (where == null) {
            where = whereName;
        } else {
            where = where + " and (" + whereName + ")";
        }
        String orderBy = MessageInfoColumns._ID + " DESC";
        String limit = String.valueOf(num);
        Cursor c = mDatabase.query(TABLE_MESSAGE, null, where, new String[]{String.valueOf(robot_id)}, null, null, orderBy, limit);
        /*Cursor c = mDatabase.query(TABLE_MESSAGE, null, where, new String[]{name}, null, null, orderBy, limit);*/

        if (c == null) {
            return new ArrayList<>();
        }
        boolean exist = c.moveToLast();
        if (!exist) {
            c.close();
            return new ArrayList<>();
        }
        ArrayList<PrivateMsg> msgs = new ArrayList<PrivateMsg>();
        PrivateMsg msg;
        Gson gson = new Gson();
        do {
            msg = new PrivateMsg();
            msg.set_id(c.getInt(c.getColumnIndex(MessageInfoColumns._ID)));
            // msg.setUser(c.getString(c.getColumnIndex(MessageInfoColumns.USER)));
            msg.setCode(c.getInt(c.getColumnIndex(MessageInfoColumns.CODE)));
            msg.setTime(c.getLong(c.getColumnIndex(MessageInfoColumns.TIME)));// 发送时间
            msg.setContent(c.getString(c.getColumnIndex(MessageInfoColumns.CONTENT)));
            msg.setType(gson.fromJson(c.getString(c.getColumnIndex(MessageInfoColumns.TYPE)), Type.class));
            msg.setUrl(c.getString(c.getColumnIndex(MessageInfoColumns.URL)));
            msg.setId(c.getString(c.getColumnIndex(MessageInfoColumns.ID)));
            msg.setTime2(c.getString(c.getColumnIndex(MessageInfoColumns.TIME2)));
            msg.setTitle(c.getString(c.getColumnIndex(MessageInfoColumns.TITLE)));
            msg.setNews_type(c.getString(c.getColumnIndex(MessageInfoColumns.NEWS_TYPE)));
            msg.setRobotId(c.getInt(c.getColumnIndex(MessageInfoColumns.ROBOT_ID)));
            msgs.add(msg);
        } while (c.moveToPrevious());
        c.close();
        return msgs;
    }

    public void insertMessageInfo(PrivateMsg msg) {
        if (msg == null) {
            return;
        }
        ContentValues values = new ContentValues();
        //values.put(MessageInfoColumns.USER, msg.getUser());
        values.put(MessageInfoColumns.CODE, msg.getCode());
        values.put(MessageInfoColumns.TIME, msg.getTime());
        values.put(MessageInfoColumns.CONTENT, msg.getContent());
        values.put(MessageInfoColumns.TYPE, new Gson().toJson(msg.getType()));
        values.put(MessageInfoColumns.URL, msg.getUrl());
        values.put(MessageInfoColumns.ID, msg.getId());
        values.put(MessageInfoColumns.TIME2, msg.getTime2());
        values.put(MessageInfoColumns.TITLE, msg.getTitle());
        values.put(MessageInfoColumns.NEWS_TYPE, msg.getNews_type());
        values.put(MessageInfoColumns.ROBOT_ID, msg.getRobotId());
        long id = mDatabase.insert(TABLE_MESSAGE, null, values);
    }

    public void insertMessageInfo(ArrayList<PrivateMsg> msgs) {
        if (msgs == null || msgs.size() == 0) {
            return;
        }
        for (PrivateMsg msg : msgs) {
            insertMessageInfo(msg);
        }
    }

    public void deleteMessageInfo(String id) {
        StringBuilder where = new StringBuilder().append(MessageInfoColumns._ID).append("=?");
        int count = mDatabase.delete(TABLE_MESSAGE, where.toString(), new String[]{id});
    }

    public void deleteMessageInfo(ArrayList<Integer> ids) {
        if (ids == null) {
            return;
        }
        int idSize = ids.size();
        if (idSize == 0) {
            return;
        }
        StringBuilder where = new StringBuilder().append(MessageInfoColumns._ID).append(" in ?");
        String[] idArray = new String[idSize];
        for (int i = 0; i < idSize; i++) {
            idArray[i] = ids.get(i).toString();
        }

        int count = mDatabase.delete(TABLE_MESSAGE, where.toString(), idArray);
    }

    public void deleteMessageInfoByMsgList(ArrayList<PrivateMsg> msgs) {
        if (msgs == null) {
            return;
        }
        int idSize = msgs.size();
        if (idSize == 0) {
            return;
        }
        StringBuilder where = new StringBuilder().append(MessageInfoColumns._ID).append(" in ");

        for (int i = 0; i < idSize; i++) {
            if (i == 0) {
                where.append("(");
            }
            where.append(msgs.get(i).get_id());
            if (i < idSize - 1) {
                where.append(",");
            } else {
                where.append(")");
            }
        }

        int count = mDatabase.delete(TABLE_MESSAGE, where.toString(), null);
    }

    public ArrayList<Robot> getRobots() {
        Cursor c = mDatabase.query(TABLE_ROBOT, null, null, null, null, null, null, null);

        if (c == null) {
            return new ArrayList<>();
        }
        boolean exist = c.moveToFirst();
        ArrayList<Robot> robots = new ArrayList<>();
        Robot robot;
        if (exist) {
            do {
                robot = new Robot();
                robot.setId(c.getInt(c.getColumnIndex(RobotColumns._ID)));
                robot.setName(c.getString(c.getColumnIndex(RobotColumns.NAME)));
                robot.setIcon(c.getString(c.getColumnIndex(RobotColumns.ICON)));
                robot.setTime(c.getLong(c.getColumnIndex(RobotColumns.TIME)));
                robot.setBirthTime(c.getLong(c.getColumnIndex(RobotColumns.BIRTHTIME)));
                robot.setWelcome(c.getString(c.getColumnIndex(RobotColumns.WELCOME)));
                robot.setSex(c.getInt(c.getColumnIndex(RobotColumns.SEX)));
                robot.setVoice(c.getInt(c.getColumnIndex(RobotColumns.VOICE)));
                robot.setBg(c.getString(c.getColumnIndex(RobotColumns.BG)));
                robot.setBgblur(c.getInt(c.getColumnIndex(RobotColumns.BGBLUR)));
                robot.setScore(c.getInt(c.getColumnIndex(RobotColumns.SCORE)));
                robot.setDesc(c.getString(c.getColumnIndex(RobotColumns.DESC)));
                robots.add(robot);
            } while (c.moveToNext());
        }
        c.close();

        return robots;
    }

    public long insertRobot(Robot robot) {
        if (robot == null) {
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(RobotColumns.NAME, robot.getName());
        values.put(RobotColumns.ICON, robot.getIcon());
        values.put(RobotColumns.TIME, robot.getTime());
        values.put(RobotColumns.BIRTHTIME, robot.getBirthTime());
        values.put(RobotColumns.WELCOME, robot.getWelcome());
        values.put(RobotColumns.SEX, robot.getSex());
        values.put(RobotColumns.VOICE, robot.getVoice());
        values.put(RobotColumns.BG, robot.getBg());
        values.put(RobotColumns.BGBLUR, robot.getBgblur());
        values.put(RobotColumns.SCORE, robot.getScore());
        values.put(RobotColumns.DESC, robot.getDesc());
        long id = mDatabase.insert(TABLE_ROBOT, null, values);
        return id;
    }

    public long updateRobot(Robot robot) {
        if (robot == null) {
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(RobotColumns.NAME, robot.getName());
        values.put(RobotColumns.ICON, robot.getIcon());
        values.put(RobotColumns.TIME, robot.getTime());
        values.put(RobotColumns.BIRTHTIME, robot.getBirthTime());
        values.put(RobotColumns.WELCOME, robot.getWelcome());
        values.put(RobotColumns.SEX, robot.getSex());
        values.put(RobotColumns.VOICE, robot.getVoice());
        values.put(RobotColumns.BG, robot.getBg());
        values.put(RobotColumns.BGBLUR, robot.getBgblur());
        values.put(RobotColumns.SCORE, robot.getScore());
        values.put(RobotColumns.DESC, robot.getDesc());
        StringBuilder where = new StringBuilder().append(MessageInfoColumns._ID).append(" = ?");
        long id = mDatabase.update(TABLE_ROBOT, values, where.toString(), new String[]{String.valueOf(robot.getId())});
        return id;
    }

    public void deleteRobot(String robotId) {
        if (robotId == null) {
            return;
        }
        StringBuilder where = new StringBuilder().append(MessageInfoColumns._ID).append(" = ?");
        int count = mDatabase.delete(TABLE_ROBOT, where.toString(), new String[]{robotId});
    }

    public ArrayList<PushMsg> getPushMsgs(int id, int num) {
        if (num <= 0) {
            num = 100;
        }
        String where = null;
        if (id > 0) {
            where = PushMsgColumns._ID + "<" + id;
        }
        String orderBy = PushMsgColumns._ID + " DESC";
        String limit = String.valueOf(num);
        Cursor c = mDatabase.query(TABLE_PUSH_MSG, null, where, null, null, null, orderBy, limit);

        if (c == null) {
            return new ArrayList<>();
        }
        boolean exist = c.moveToFirst();
        ArrayList<PushMsg> pushMsgs = new ArrayList<>();
        PushMsg pushMsg;
        if (exist) {
            do {
                pushMsg = new PushMsg();
                pushMsg.set_id(c.getInt(c.getColumnIndex(PushMsgColumns._ID)));
                pushMsg.setTime(c.getLong(c.getColumnIndex(PushMsgColumns.TIME)));
                pushMsg.setAction(c.getInt(c.getColumnIndex(PushMsgColumns.ACTION)));
                pushMsg.setUrl(c.getString(c.getColumnIndex(PushMsgColumns.URL)));
                pushMsg.setPic_url(c.getString(c.getColumnIndex(PushMsgColumns.PIC_URL)));
                pushMsg.setMsg_time(c.getString(c.getColumnIndex(PushMsgColumns.MSG_TIME)));
                pushMsg.setContent_pic_url(c.getString(c.getColumnIndex(PushMsgColumns.CONTENT_PIC_URL)));
                pushMsg.setContent_url(c.getString(c.getColumnIndex(PushMsgColumns.CONTENT_URL)));
                pushMsg.setContent_url_hint(c.getString(c.getColumnIndex(PushMsgColumns.CONTENT_URL_HINT)));
                pushMsg.setTitle(c.getString(c.getColumnIndex(PushMsgColumns.TITLE)));
                pushMsg.setContent(c.getString(c.getColumnIndex(PushMsgColumns.CONTENT)));
                pushMsg.setExtras(c.getString(c.getColumnIndex(PushMsgColumns.EXTRAS)));
                pushMsg.setMsgId(c.getString(c.getColumnIndex(PushMsgColumns.MSG_ID)));
                pushMsg.setDesc(c.getString(c.getColumnIndex(PushMsgColumns.DESC)));
                pushMsg.setUnread(c.getInt(c.getColumnIndex(PushMsgColumns.UNREAD)));
                pushMsgs.add(pushMsg);
            } while (c.moveToNext());
        }
        c.close();

        return pushMsgs;
    }

    /**
     *
     * @param pushMsg
     * @param hasExtras 是否需要处理extras
     * @return
     */
    public long insertPushMsg(PushMsg pushMsg, boolean hasExtras) {
        String extras = pushMsg.getExtras();
        try {
            JSONObject jsonObject = new JSONObject(extras);
            if(TextUtils.isEmpty(pushMsg.getTitle())) {
                pushMsg.setTitle(jsonObject.optString("title"));
            }
            pushMsg.setAction(jsonObject.optInt("action"));
            pushMsg.setUrl(jsonObject.optString("url"));
            pushMsg.setPic_url(jsonObject.optString("pic_url"));
            pushMsg.setMsg_time(jsonObject.optString("msg_time"));
            pushMsg.setContent_pic_url(jsonObject.optString("content_pic_url"));
            pushMsg.setContent_url(jsonObject.optString("content_url"));
            pushMsg.setContent_url_hint(jsonObject.optString("content_url_hint"));
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return insertPushMsg(pushMsg);
    }

    private long insertPushMsg(PushMsg pushMsg) {
        if (pushMsg == null) {
            return -1;
        }
        ContentValues values = new ContentValues();
        values.put(PushMsgColumns.TIME, pushMsg.getTime());
        values.put(PushMsgColumns.ACTION, pushMsg.getAction());
        values.put(PushMsgColumns.URL, pushMsg.getUrl());
        values.put(PushMsgColumns.PIC_URL, pushMsg.getPic_url());
        values.put(PushMsgColumns.MSG_TIME, pushMsg.getMsg_time());
        values.put(PushMsgColumns.CONTENT_PIC_URL, pushMsg.getContent_pic_url());
        values.put(PushMsgColumns.CONTENT_URL, pushMsg.getContent_url());
        values.put(PushMsgColumns.CONTENT_URL_HINT, pushMsg.getContent_url_hint());
        values.put(PushMsgColumns.TITLE, pushMsg.getTitle());
        values.put(PushMsgColumns.CONTENT, pushMsg.getContent());
        values.put(PushMsgColumns.EXTRAS, pushMsg.getExtras());
        values.put(PushMsgColumns.MSG_ID, pushMsg.getMsgId());
        values.put(PushMsgColumns.DESC, pushMsg.getDesc());
        values.put(PushMsgColumns.UNREAD, pushMsg.getUnread());
        long id = mDatabase.insert(TABLE_PUSH_MSG, null, values);
        return id;
    }

    public long updatePushMSg(int _id, int unRead) {
        ContentValues values = new ContentValues();
        values.put(PushMsgColumns.UNREAD, unRead);
        StringBuilder where = new StringBuilder().append(PushMsgColumns._ID).append(" = ?");
        long id = mDatabase.update(TABLE_PUSH_MSG, values, where.toString(), new String[]{String.valueOf(_id)});
        return id;
    }


    public void deletePushMsg(String _id) {
        if (_id == null) {
            return;
        }
        StringBuilder where = new StringBuilder().append(PushMsgColumns._ID).append(" = ?");
        int count = mDatabase.delete(TABLE_PUSH_MSG, where.toString(), new String[]{_id});
    }
}
