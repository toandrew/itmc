package com.infthink.itmc.v2.type;

import java.io.Serializable;

public class PersonInfo implements Serializable {
    private static final long serialVersionUID = 1L;
    public String alias;
    public ImageUrlInfo bigImageUrl;
    public String country;
    public String cv;
    public String nameCn;
    public String nameEn;

    public String getName() {
        return "Person";
        // StringBuilder localStringBuilder = new StringBuilder();
        // if ((!Util.isEmpty(this.nameCn)) &&
        // (this.nameCn.compareToIgnoreCase("none") != 0))
        // localStringBuilder.append(this.nameCn);
        // while (true)
        // {
        // if ((!Util.isEmpty(this.alias)) &&
        // (this.alias.compareToIgnoreCase("none") != 0))
        // localStringBuilder.append("(" + this.alias + "");
        // return localStringBuilder.toString();
        // if ((Util.isEmpty(this.nameEn)) ||
        // (this.nameEn.compareToIgnoreCase("none") == 0))
        // continue;
        // localStringBuilder.append(this.nameEn);
        // }
    }
}
