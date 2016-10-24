package com.evader.rookies.lingo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bshah on 10/23/2016.
 * The UrbanDictionary class provides definitions
 */

public class UrbanDefinition {
    private String term;
    private String definition;

    UrbanDefinition(String term, String definition){
        this.term = term;
        this.definition = definition;
    }

    UrbanDefinition(){
        term = null;
        definition = null;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }
}
