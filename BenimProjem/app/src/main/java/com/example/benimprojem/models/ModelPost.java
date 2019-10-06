package com.example.benimprojem.models;

public class ModelPost {

    public static boolean get;
    String pId, pTitle, pTime, pDescr, pLikes,pSaved, pComments, pImage, uid, uEmail, uDp, uName;

    public ModelPost() {
    }

    public ModelPost(String pId, String pTitle, String pTime, String pDescr, String pLikes, String pSaved, String pComments, String pImage, String uid, String uEmail, String uDp, String uName) {
        this.pId = pId;
        this.pTitle = pTitle;
        this.pTime = pTime;
        this.pDescr = pDescr;
        this.pLikes = pLikes;
        this.pSaved = pSaved;
        this.pComments = pComments;
        this.pImage = pImage;
        this.uid = uid;
        this.uEmail = uEmail;
        this.uDp = uDp;
        this.uName = uName;
    }


    public static boolean isGet() {
        return get;
    }

    public static void setGet(boolean get) {
        ModelPost.get = get;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpTitle() {
        return pTitle;
    }

    public void setpTitle(String pTitle) {
        this.pTitle = pTitle;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpDescr() {
        return pDescr;
    }

    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    public String getpLikes() {
        return pLikes;
    }

    public void setpLikes(String pLikes) {
        this.pLikes = pLikes;
    }

    public String getpSaved() {
        return pSaved;
    }

    public void setpSaved(String pSaved) {
        this.pSaved = pSaved;
    }

    public String getpComments() {
        return pComments;
    }

    public void setpComments(String pComments) {
        this.pComments = pComments;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getuEmail() {
        return uEmail;
    }

    public void setuEmail(String uEmail) {
        this.uEmail = uEmail;
    }

    public String getuDp() {
        return uDp;
    }

    public void setuDp(String uDp) {
        this.uDp = uDp;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }
}