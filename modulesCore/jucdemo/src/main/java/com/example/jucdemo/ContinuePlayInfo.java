package com.example.jucdemo;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangmei on 17/10/26.
 */

public class ContinuePlayInfo {

    private int errno;
    private String errmsg;
    private Data data;
    private String logid;
    @SerializedName("spend_time")
    private String spendTime;

    public int getErrno() {
        return errno;
    }

    public void setErrno(int errno) {
        this.errno = errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getLogid() {
        return logid;
    }

    public void setLogid(String logid) {
        this.logid = logid;
    }

    public String getSpendTime() {
        return spendTime;
    }

    public void setSpendTime(String spendTime) {
        this.spendTime = spendTime;
    }

    public class Data {
        private String id;
        private int type;
        private String title;
        private String url;
        @SerializedName("message_id")
        private String messageId;
        private int favorite;
        private String singer;
        private int duration;
        private int format;
        private String album;
        @SerializedName("album_id")
        private long albumId;
        @SerializedName("offset_ms")
        private long offsetMs;
        private String pic;
        @SerializedName("data_resource")
        private int dataResource;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getMessageId() {
            return messageId;
        }

        public void setMessageId(String messageId) {
            this.messageId = messageId;
        }

        public int getFavorite() {
            return favorite;
        }

        public void setFavorite(int favorite) {
            this.favorite = favorite;
        }

        public String getSinger() {
            return singer;
        }

        public void setSinger(String singer) {
            this.singer = singer;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public int getFormat() {
            return format;
        }

        public void setFormat(int format) {
            this.format = format;
        }

        public String getAlbum() {
            return album;
        }

        public void setAlbum(String album) {
            this.album = album;
        }

        public long getAlbumId() {
            return albumId;
        }

        public void setAlbumId(long albumId) {
            this.albumId = albumId;
        }

        public long getOffsetMs() {
            return offsetMs;
        }

        public void setOffsetMs(long offsetMs) {
            this.offsetMs = offsetMs;
        }

        public String getPic() {
            return pic;
        }

        public void setPic(String pic) {
            this.pic = pic;
        }

        public int getDataResource() {
            return dataResource;
        }

        public void setDataResource(int dataResource) {
            this.dataResource = dataResource;
        }

    }
}
