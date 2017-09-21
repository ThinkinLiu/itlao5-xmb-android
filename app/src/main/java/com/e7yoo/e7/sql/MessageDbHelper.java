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
import com.e7yoo.e7.model.Robot;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MessageDbHelper extends SQLiteOpenHelper {
    @SuppressWarnings("unused")
    private static final String TAG = "MessageDbHelper";
    private static final String DB_NAME = "db_e7yoo_info";
    private static final int DB_VERSION = 3;
    private static final String TABLE_MESSAGE = "t_message_info";
    private static final String TABLE_FAVORITE = "t_favorite_info";
    private static final String TABLE_ROBOT = "t_robot";

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
            .append(RobotColumns.SCORE).append(" INTEGER,")
            .append(RobotColumns.DESC).append(" TEXT")
            .append(")");

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(sb_message.toString());
        db.execSQL(sql_favorite.toString());
        db.execSQL(sql_robot.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < newVersion) {
            if (oldVersion == 1) {
                // 数据库版本1基础上增加robot表，message表增加robot_id列，其他数据库未改变
                db.execSQL(sql_robot.toString());
                db.execSQL("ALTER TABLE "+ TABLE_MESSAGE + " ADD " + MessageInfoColumns.ROBOT_ID + " INTEGER;");
            } else {
                // 其他情况，将表销毁再创建（后续版本可自行修改）
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITE);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROBOT);
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
        private static final String SCORE = "score";
        private static final String DESC = "desc"; // 备用

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
            return null;
        }
        boolean exist = c.moveToLast();
        if (!exist) {
            c.close();
            return null;
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
            return null;
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
}
