package com.evader.rookies.lingo;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by bshah on 10/23/2016.
 * The UrbanDictionary class provides definitions
 */

public class UrbanDefinition implements Parcelable {
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

    // Parcelling part
    public UrbanDefinition(Parcel in){
        String[] data = new String[2];

        in.readStringArray(data);
        this.term = data[0];
        this.definition = data[1];
    }

    public int describeContents(){
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {this.term,
                this.definition});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public UrbanDefinition createFromParcel(Parcel in) {
            return new UrbanDefinition(in);
        }

        public UrbanDefinition[] newArray(int size) {
            return new UrbanDefinition[size];
        }
    };
}
