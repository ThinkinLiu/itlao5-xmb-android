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
        gameInfos.add(new GameInfo(context.getString(R.string.gamelist_game2048), context.getString(R.string.gamelist_game2048_content), 0, null, Game2048Activity.class.getSimpleName()));
        gameInfos.add(new GameInfo(context.getString(R.string.gamelist_killbird), context.getString(R.string.gamelist_killbird_content), 0, null, KillBirdActivity.class.getSimpleName()));
        gameInfos.add(new GameInfo(context.getString(R.string.gamelist_plane), context.getString(R.string.gamelist_plane_content), 0, null, PlaneMainActivity.class.getSimpleName()));
        gameInfos.add(new GameInfo(context.getString(R.string.game_h5_zhaoyaojing), context.getString(R.string.game_h5_zhaoyaojing_content), 1, "http://h.4399.com/play/161856.htm", null));
        gameInfos.add(new GameInfo(context.getString(R.string.game_h5_zaqiche), context.getString(R.string.game_h5_zaqiche_content), 1, "http://h.4399.com/play/159875.htm", null));
        gameInfos.add(new GameInfo(context.getString(R.string.game_h5_wucaiguodong), context.getString(R.string.game_h5_wucaiguodong_content), 2, "http://h.4399.com/play/191530.htm", null));
        gameInfos.add(new GameInfo(context.getString(R.string.game_h5_guoshanche), context.getString(R.string.game_h5_guoshanche_content), 2, "http://h.4399.com/play/158682.htm", null));
        return gameInfos;
    }
}
