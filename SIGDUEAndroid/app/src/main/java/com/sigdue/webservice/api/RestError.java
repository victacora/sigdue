package com.sigdue.webservice.api;

import org.json.JSONException;
import org.json.JSONObject;
import uk.co.senab.photoview.BuildConfig;

import static android.app.Notification.CATEGORY_STATUS;

public class RestError {
    private String error;
    private boolean status;

    public RestError(String error) {
        this.status = false;
        this.error = error;
    }

    public boolean isStatus() {
        return this.status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getError() {
        return this.error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("error", this.error);
            jsonObject.put(CATEGORY_STATUS, this.status);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return BuildConfig.FLAVOR;
        }
    }
}
