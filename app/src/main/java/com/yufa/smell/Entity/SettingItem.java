package com.yufa.smell.Entity;

import java.io.Serializable;

/**
 * Created by luyufa on 2016/12/4.
 */

public class SettingItem implements Serializable {

    private String text;

    public SettingItem() {
    }

    public SettingItem(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
