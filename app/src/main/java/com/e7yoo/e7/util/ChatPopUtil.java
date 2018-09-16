package com.e7yoo.e7.util;

import com.e7yoo.e7.R;

/**
 * 聊天页面资源类
 */
public class ChatPopUtil {
    private static final String[] chatPopsName = {"默认"};
    private static final int[] chatPopsImg = {R.drawable.chat_pop_me};
    private static final int[][] chatPops = {{R.drawable.chat_pop_me_seletor, R.drawable.chat_pop_meng_seletor, R.drawable.chat_item_voice_selector}};
    private static final int[][] chatPopTextColors = {{0xffffff, 0x333333, 0x666666}};
    private static int chatPopNum = -1;

    private static ChatPopUtil instance;

    private ChatPopUtil() {
    }

    public static ChatPopUtil getInstance() {
        if(instance == null) {
            synchronized (ChatPopUtil.class) {
                if(instance == null) {
                    instance = new ChatPopUtil();
                }
            }
        }
        return instance;
    }

    public static void init() {
        chatPopNum = PreferenceUtil.getInt(Constant.PREFERENCE_CHAT_POP_NUM, 0);
        if(chatPopNum > chatPops.length) {
            chatPopNum = chatPops.length;
        }
        if(chatPopNum == chatPops.length) { // 随机出现，每一次打开都不同
            chatPopNum = RandomUtil.getRandomNum(chatPops.length) % chatPops.length;
        }
    }

    /**
     * 名称
     * @return
     */
    public String getChatPopName() {
        return chatPopsName[getChatPopNum()];
    }

    /**
     * 示例图
     * @return
     */
    public int getChatPopImg() {
        return chatPopsImg[getChatPopNum()];
    }

    /**
     * 返回COLOR值，如0x000000
     * 0 发送
     * 1 接收
     * 2 时间等提示文字
     * @return
     */
    public int[] getChatPopTextColor() {
        return chatPopTextColors[getChatPopNum()];
    }

    /**
     * 0 发送
     * 1 接收
     * 2 声音
     * @return
     */
    public int[] getChatPop() {
        return chatPops[getChatPopNum()];
    }

    public void setChatPopNum(int chatPopNum) {
        if(chatPopNum < 0) {
            chatPopNum = 0;
        } else if(chatPopNum > chatPops.length) {
            chatPopNum = chatPops.length;
        }
        this.chatPopNum = chatPopNum;
        PreferenceUtil.commitInt(Constant.PREFERENCE_CHAT_POP_NUM, chatPopNum);
    }

    public int getChatPopNum() {
        if(chatPopNum < 0) {
            init();
        }
        return chatPopNum;
    }
}
