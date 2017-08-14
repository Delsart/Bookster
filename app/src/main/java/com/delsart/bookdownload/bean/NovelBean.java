package com.delsart.bookdownload.bean;

public class NovelBean {
    private String name;
    private String time;
    private String info;
    private String category;
    private String status;
    private String author;
    private String words;
    private String pic;
    private String showText;
    private String downloadFromUrl;

    public NovelBean(String name, String time, String info, String category, String status, String author, String words, String pic, String downloadFromUrl) {
        this.name = name;
        this.time = time;
        this.info = info;
        this.category = category;
        this.status = status;
        this.author = author;
        this.words = words;
        this.pic = pic;
        this.downloadFromUrl = downloadFromUrl;

        this.showText = (author.equals("") ? "" : author + "\n") +
                (category.equals("") ? "" : category + "\n") +
                (words.equals("") ? "" : words + "\n") +
                (status.equals("") ? "" : status + "\n") +
                (time.equals("") ? "" : time + "\n") +
                (info.equals("") ? "\n" + "无简介" : "\n" + info);
    }

    public String getDownloadFromUrl() {
        return downloadFromUrl;
    }

    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getInfo() {
        return info;
    }

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getAuthor() {
        return author;
    }

    public String getWords() {
        return words;
    }

    public String getPic() {
        return pic;
    }


    public String getShowText() {
        return showText;
    }
}
