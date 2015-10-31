package com.bandotheapp.bando.comments;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by benjamin.harvey on 10/31/15.
 */
@ParseClassName("Comment")
public class Comment extends ParseObject {
    public Comment() {
        // A default constructor is required.
    }

    public String getDisplayName() {
        return getString("displayName");
    }
    public void setDisplayName(String displayName) {
        put("displayName", displayName);
    }

    public String getKey() {
        return getString("key");
    }
    public void setKey(String key) {
        put("key", key);
    }

    public ParseUser getOwner() {
        return getParseUser("owner");
    }
    public void setOwner(ParseUser user) {
        put("owner", user);
    }

    public void setText(String text) {
        // Ah, that takes me back!
        put("comment",text);
    }
    public String getText(){
        return getString("comment");
    }
}