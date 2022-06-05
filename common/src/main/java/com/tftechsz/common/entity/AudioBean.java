package com.tftechsz.common.entity;

import java.io.Serializable;

/**
 * <pre>
 *     @author yangchong
 *     blog  : www.pedaily.cn
 *     time  : 2018/03/22
 *     desc  : 音频单曲信息
 *     revise:
 * </pre>
 */
public class AudioBean implements Serializable {

    // 歌曲类型:本地/网络
    private Type type;
    // [本地歌曲]歌曲id
    private long id;
    // 音乐标题
    private String title;
    // 艺术家
    private String artist;
    // 专辑
    private String album;
    // [本地歌曲]专辑ID
    private long albumId;
    // [在线歌曲]专辑封面路径
    private String coverPath;
    // 持续时间
    private long duration;
    // 音乐路径   本地or网络
    private String oss_url;
    // 文件名
    private String fileName;
    // 文件大小
    private long size;
    //文件哈希
    private String file_hash;
    //状态   1审核通过 0审核中 2未通过
    private int status = -1;

    //是否选中
    private boolean isChecked;
    //是否已经上传
    private boolean isUpload;
    //播放状态  0:未播放   1播放   2暂停
    private int play_status;

    public int getStatus() {
        return status;// 1审核通过 0审核中 2未通过
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getFileHash() {
        return file_hash;
    }

    public void setFileHash(String file_hash) {
        this.file_hash = file_hash;
    }

    public boolean isPlaying() {
        return play_status == 1;
    }

    public boolean isPlayPause() {
        return play_status == 2;
    }

    public void setPlaying(boolean playing) {
        play_status = playing ? 1 : 0;
    }

    public void setPlayPause() {
        play_status = 2;
    }

    public boolean isUpload() {
        return isUpload;
    }

    public void setUpload(boolean upload) {
        isUpload = upload;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public enum Type {
        LOCAL,
        ONLINE
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getId() {
        return String.valueOf(id);
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
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

    public String getCoverPath() {
        return coverPath;
    }

    public void setCoverPath(String coverPath) {
        this.coverPath = coverPath;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getPath() {
        return oss_url;
    }

    public void setPath(String path) {
        this.oss_url = path;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return size;
    }

    public void setFileSize(long fileSize) {
        this.size = fileSize;
    }

    /**
     * 思考为什么要重写这两个方法
     * 对比本地歌曲是否相同
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AudioBean) {
            AudioBean bean = (AudioBean) obj;
            return getId().equals(bean.getId());
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

}
