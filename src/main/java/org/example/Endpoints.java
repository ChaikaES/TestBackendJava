package org.example;

public class Endpoints {
    public static final String UPLOAD_IMAGE = "/image";
    public static final String DELETE_IMAGE = "/account/{username}/image/{deleteHash}";
    public static final String ALBUM_DETAIL = "/album/{albumHash}";
    public static final String ALBUM_LIST = "/album";
    public static final String ACCOUNT_DETAIL = "/account/{username}";
}
