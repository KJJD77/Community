package com.kjjd.community.community.entity;


public class Page {
    private int current=1;
    private int rows;
    private int limit=10;
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1)
        this.current = current;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if(rows>=0)
        this.rows = rows;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1&&limit<=100)
            this.limit = limit;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    public int getOffset()
    {
        return limit*(current-1);
    }
    public int getTotal()
    {
        return (rows+limit-1)/limit;
    }
    public int getFrom()
    {
        int from=current-2;
        return from > 1 ? from : 1;
    }
    public int getTo()
    {
        int to=current+2;
        int total=getTotal();
        return to < total ? to : total;
    }


}
