package com.evader.rookies.lingo;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by bshah on 10/23/2016.
 */

public class Statuses {
    @SerializedName("statuses")
    public List<Tweet> twits;
}
