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
        gameInfo.setUmengKey(UmengUtil.GAME_2048);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.gamelist_killbird), context.getString(R.string.gamelist_killbird_content), 0, null, KillBirdActivity.class.getSimpleName());
        gameInfo.setIconResId(R.mipmap.game_killbird);
        gameInfo.setUmengKey(UmengUtil.GAME_BIRD);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.gamelist_plane), context.getString(R.string.gamelist_plane_content), 0, null, PlaneMainActivity.class.getSimpleName());
        gameInfo.setIconResId(R.mipmap.game_plane);
        gameInfo.setUmengKey(UmengUtil.GAME_PLANE);
        gameInfos.add(gameInfo);
        gameInfos.add(getZhaoyaojing(context));
        gameInfo = new GameInfo(context.getString(R.string.game_h5_zaqiche), context.getString(R.string.game_h5_zaqiche_content), 1, "http://h.4399.com/play/159875.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_zaqiche_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_zaqiche_share_content));
        gameInfo.setIcon("http://imga1.5054399.com/upload_pic/2016/3/15/4399_14520189468.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_ZQC);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_wucaiguodong), context.getString(R.string.game_h5_wucaiguodong_content), 2, "http://h.4399.com/play/191530.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_wucaiguodong_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_wucaiguodong_share_content));
        gameInfo.setIcon("http://imga1.5054399.com/upload_pic/2017/9/28/4399_15142086906.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_WCGD);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_guoshanche), context.getString(R.string.game_h5_guoshanche_content), 2, "http://h.4399.com/play/158682.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_guoshanche_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_guoshanche_share_content));
        gameInfo.setIcon("http://imga4.5054399.com/upload_pic/2017/2/22/4399_14113059688.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_GSC);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_kengdie), context.getString(R.string.game_h5_kengdie_content), 1, "http://h.4399.com/play/175320.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_kengdie_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_kengdie_share_content));
        gameInfo.setIcon("http://imga1.5054399.com/upload_pic/2016/6/3/4399_17021821034.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_KD);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_baikuai), context.getString(R.string.game_h5_baikuai_content),1, "http://h.4399.com/play/154247.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_baikuai_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_baikuai_share_content));
        gameInfo.setIcon("http://imga3.5054399.com/upload_pic/2016/11/11/4399_14302471814.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_BK);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_chazhen), context.getString(R.string.game_h5_chazhen_content), 1, "http://h.4399.com/play/190181.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_chazhen_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_chazhen_share_content));
        gameInfo.setIcon("http://imga2.5054399.com/upload_pic/2017/8/14/4399_16002464962.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_JFCJ);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_motuo), context.getString(R.string.game_h5_motuo_content), 2, "http://h.4399.com/play/166087.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_motuo_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_motuo_share_content));
        gameInfo.setIcon("http://imga3.5054399.com/upload_pic/2015/11/23/4399_15592890967.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_JXMT);
        gameInfos.add(gameInfo);
        gameInfo = new GameInfo(context.getString(R.string.game_h5_water), context.getString(R.string.game_h5_water_content), 1, "http://h.4399.com/play/191276.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_water_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_water_share_content));
        gameInfo.setIcon("http://imga2.5054399.com/upload_pic/2017/9/18/4399_09084820108.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_JXMT);
        gameInfos.add(gameInfo);
        return gameInfos;
    }

    public static GameInfo getZhaoyaojing(Context context) {
        GameInfo gameInfo = new GameInfo(context.getString(R.string.game_h5_zhaoyaojing), context.getString(R.string.game_h5_zhaoyaojing_content), 1, "http://h.4399.com/play/161856.htm", null);
        gameInfo.setShare_title(context.getString(R.string.game_h5_zhaoyaojing_share));
        gameInfo.setShare_content(context.getString(R.string.game_h5_zhaoyaojing_share_content));
        gameInfo.setIcon("http://imga2.5054399.com/upload_pic/2015/8/31/4399_15445940405.jpg");
        gameInfo.setUmengKey(UmengUtil.GAME_ZYJ);
        return gameInfo;
    }
}
