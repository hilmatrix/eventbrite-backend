package com.nurmanhilman.eventbrite.requests;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TemplateRequest {
    public boolean isValid;
    public List<String> errorList;
    Map<String, Object> request;

    public TemplateRequest() {
        isValid = true;
        errorList = new ArrayList<>();
    }

    public TemplateRequest addError(String error) {
        isValid = false;
        errorList.add(error);
        return this;
    }

    public boolean isEmpty(String data) {
        return (data == null) || (data.isEmpty());
    }

    public boolean isEmpty(Long data) {
        return (data == null);
    }

    public boolean isEmpty(Integer data) {
        return (data == null);
    }

    public String getField(String field) {
        return (String) request.get(field);
    }
}
