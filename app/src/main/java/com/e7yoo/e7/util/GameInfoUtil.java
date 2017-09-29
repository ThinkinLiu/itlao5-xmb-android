package com.e7yoo.e7.util;

import android.content.Context;

import com.e7yoo.e7.R;
import com.e7yoo.e7.game.game2048.Game2048Activity;
import com.e7yoo.e7.game.killbird.KillBirdActivity;
import com.e7yoo.e7.game.plane.PlaneMainActivity;
import com.e7yoo.e7.model.GameInfo;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/9/28.
 */

public class GameInfoUtil {
    public static ArrayList<GameInfo> getGameInfos(Context context) {
        ArrayList<GameInfo> gameInfos = new ArrayList<>();
        GameInfo gameInfo = new GameInfo(context.getString(R.string.gamelist_game2048), context.getString(R.string.gamelist_game2048_content), 0, null, Game2048Activity.class.getSimpleName());
        gameInfo.setIconResId(R.mipmap.game_2048);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.gamelist_killbird), context.getString(R.string.gamelist_killbird_content), 0, null, KillBirdActivity.class.getSimpleName());
        gameInfo.setIconResId(R.mipmap.game_killbird);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.gamelist_plane), context.getString(R.string.gamelist_plane_content), 0, null, PlaneMainActivity.class.getSimpleName());
        gameInfo.setIconResId(R.mipmap.game_plane);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_zhaoyaojing), context.getString(R.string.game_h5_zhaoyaojing_content), 1, "http://h.4399.com/play/161856.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_zhaoyaojing_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_zhaoyaojing_share_content));
        gameInfo.setIcon("http://imga2.5054399.com/upload_pic/2015/8/31/4399_15445940405.jpg");
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_zaqiche), context.getString(R.string.game_h5_zaqiche_content), 1, "http://h.4399.com/play/159875.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_zaqiche_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_zaqiche_share_content));
        gameInfo.setIcon("http://imga1.5054399.com/upload_pic/2016/3/15/4399_14520189468.jpg");
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_wucaiguodong), context.getString(R.string.game_h5_wucaiguodong_content), 2, "http://h.4399.com/play/191530.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_wucaiguodong_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_wucaiguodong_share_content));
        gameInfo.setIcon("http://imga1.5054399.com/upload_pic/2017/9/28/4399_15142086906.jpg");
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_guoshanche), context.getString(R.string.game_h5_guoshanche_content), 2, "http://h.4399.com/play/158682.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_guoshanche_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_guoshanche_share_content));
        gameInfo.setIcon("http://imga4.5054399.com/upload_pic/2017/2/22/4399_14113059688.jpg");
        gameInfos.add(gameInfo);
        return gameInfos;
    }
}
