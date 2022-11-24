package com.bae.myapplication.datasystem;

import java.io.Serializable;
import java.util.Date;

/**
 * A super class that represents objects DataStore can store
 */
public class DataModule implements Serializable {

    private String name;
    private Date date;

    public String getName()
    {
        return name;
    }

    public Date getDate()
    {
        return date;
    }

    public void setName(String uKey) {
        name = uKey;
    }

    public void setDate(Date dateCreated) {
        date = dateCreated;
    }
}