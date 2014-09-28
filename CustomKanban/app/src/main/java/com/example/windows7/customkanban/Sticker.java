package com.example.windows7.customkanban;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Windows7 on 25.09.2014.
 */
public class Sticker implements Comparable<Sticker>{

    private String title;
    private String description;
    private String creationDate;
    private int priority;
    private CURRENT_LOCATON location;


    public Sticker(String title, String description, int priority) {
        this.title = title;
        this.description = description;
        this.priority = priority;

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        this.creationDate = dateFormat.format(cal.getTime());
        this.location = CURRENT_LOCATON.Planed;

    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getCreationDate() {
        return this.creationDate;
    }

    public String getPriority() {
        return this.priority +"";
    }

    public CURRENT_LOCATON getLocation() {
        return this.location;
    }

    public String getDate()
    {
        return this.creationDate;
    }


    public void setDate(String newDate)
    {
        this.creationDate = newDate;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public void setDescription(String newDescription) {
        this.description = newDescription;
    }

    public void setLocation(CURRENT_LOCATON newLocation) {
        this.location = newLocation;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    //метод для сортировки
    @Override
    public int compareTo(Sticker another) {

        int comparePriority = another.priority;
        return this.priority - comparePriority;
    }
}
