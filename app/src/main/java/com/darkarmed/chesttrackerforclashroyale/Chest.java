package com.darkarmed.chesttrackerforclashroyale;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Xu on 5/20/16.
 */
public class Chest implements Cloneable {

    public enum Type {
        SILVER, GOLDEN, GIANT, MAGICAL, SUPER_MAGICAL, MULTI
    }
    public enum Status {
        LOCKED, SKIPPED, OPENED
    }

    private Integer mIndex = 0;

    private Type mType = Type.SILVER;
    private Status mStatus = Status.LOCKED;
    private Boolean mMatched = null;

    private Integer mThumb;
    private Integer mThumbLocked;

    private Map<Type, Integer> mTypes;

    private Date mDate = new Date();

    Chest(Integer index, Type type, Status status) {
        this.mIndex = index;
        this.mType = type;
        this.mStatus = status;
        this.mTypes = new HashMap<>();
    }

    Chest(Integer index, Type type) {
        this(index, type, Status.LOCKED);
    }

    Chest(Integer index, char c) {
        this.mIndex = index;
        this.mType = char2Type(c);
        this.mStatus = Chest.Status.LOCKED;
        this.mTypes = new HashMap<>();
    }

    public Integer getIndex() {
        return mIndex;
    }

    public void setIndex(Integer index) {
        mIndex = index;
    }

    public Type getType() {
        return mType;
    }

    public void setType(Type type) {
        mType = type;
    }

    public Status getStatus() {
        return mStatus;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public Boolean getMatched() {
        return mMatched;
    }

    public void setMatched(boolean matched) {
        mMatched = matched;
    }

    public Integer getThumb() {
        loadThumb();
        if (mStatus == Status.LOCKED) {
            return mThumbLocked;
        } else {
            return mThumb;
        }
    }

    private void loadThumb() {
        switch (mType) {
            case SILVER:
                mThumb = R.drawable.silver_chest;
                mThumbLocked = R.drawable.silver_chest_locked;
                break;
            case GOLDEN:
                mThumb = R.drawable.golden_chest;
                mThumbLocked = R.drawable.golden_chest_locked;
                break;
            case GIANT:
                mThumb = R.drawable.giant_chest;
                mThumbLocked = R.drawable.giant_chest_locked;
                break;
            case MAGICAL:
                mThumb = R.drawable.magical_chest;
                mThumbLocked = R.drawable.magical_chest_locked;
                break;
            case MULTI:
                mThumb = R.drawable.multi_chest;
                mThumbLocked = R.drawable.multi_chest;
                break;
            default:
                mThumb = R.drawable.silver_chest;
                mThumbLocked = R.drawable.silver_chest_locked;
        }
    }

    public void addTypeCount(Type type, int num) {
        if (mTypes.containsKey(type)) {
            mTypes.put(type, mTypes.get(type) + num);
        } else {
            mTypes.put(type, num);
        }
    }

    public void addTypeCount(char c, int num) {
        addTypeCount(char2Type(c), num);
    }

    public Map<Type, Integer> getTypes() {
        return mTypes;
    }

    private Type char2Type(char c) {
        switch (c) {
            case 's':
                return Type.SILVER;
            case 'g':
                return Type.GOLDEN;
            case 'G':
                return Type.GIANT;
            case 'm':
                return Type.MAGICAL;
            case 'M':
                return Type.SUPER_MAGICAL;
            case 'x':
                return Type.MULTI;
            default:
                return Type.SILVER;
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        Chest chest = (Chest)super.clone();
        chest.mIndex = this.mIndex;
        chest.mType = this.mType;
        chest.mStatus = this.mStatus;
        chest.mDate = new Date();
        return chest;
    }
}
