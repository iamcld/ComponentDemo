package com.example.jucdemo;

import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Created by huangmei on 17/10/26.
 */

public class CommandInfo extends Status {
    private List<CommandBean> result;

    public List<CommandBean> getResult() {
        return result;
    }

    public void setResult(List<CommandBean> result) {
        this.result = result;
    }

    public class CommandBean {
        @SerializedName("card_type")
        private String cardType;
        private String intent;
        private String status;
        private String object;
        @SerializedName("tts_status")
        private TTSStatusBean ttsStatus;

        public String getCardType() {
            return cardType;
        }

        public void setCardType(String cardType) {
            this.cardType = cardType;
        }

        public String getIntent() {
            return intent;
        }

        public void setIntent(String intent) {
            this.intent = intent;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public TTSStatusBean getTtsStatus() {
            return ttsStatus;
        }

        public String getObject() {
            return object;
        }

        public void setObject(String object) {
            this.object = object;
        }

        public void setTtsStatus(TTSStatusBean ttsStatus) {
            this.ttsStatus = ttsStatus;
        }

        public class TTSStatusBean {
            private String tts;

            public String getTts() {
                return tts;
            }

            public void setTts(String tts) {
                this.tts = tts;
            }
        }

    }

}
