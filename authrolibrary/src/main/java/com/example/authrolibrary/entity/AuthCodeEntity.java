package com.example.authrolibrary.entity;

public class AuthCodeEntity {


    /**
     * errorCode : 0
     * errorMsg : 成功
     * body : {"authCode":"Ald7tvJjDXtYhsUlrf6kyv9B3OMDzwkqZEfI6vhS5KViCKVaUpAanmDvpF/UOMsCOplrYBoBIMVGB632EnVkohUYFYaBJTqBkdyzM+1aTEVT/Dxky/y2cK3m84EOSKkU4wgr84+FqSQnSck3poo/jjYubXv86HYisTX0ICPwEek="}
     */

    private int errorCode;
    private String errorMsg;
    private BodyBean body;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
        this.body = body;
    }

    public static class BodyBean {
        /**
         * authCode : Ald7tvJjDXtYhsUlrf6kyv9B3OMDzwkqZEfI6vhS5KViCKVaUpAanmDvpF/UOMsCOplrYBoBIMVGB632EnVkohUYFYaBJTqBkdyzM+1aTEVT/Dxky/y2cK3m84EOSKkU4wgr84+FqSQnSck3poo/jjYubXv86HYisTX0ICPwEek=
         */

        private String authCode;

        public String getAuthCode() {
            return authCode;
        }

        public void setAuthCode(String authCode) {
            this.authCode = authCode;
        }
    }
}
