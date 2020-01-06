package cz.cas.lib.vzb.exception;

import core.exception.GeneralException;

import java.util.HashMap;
import java.util.Map;

public class MissingDataInRecordException extends GeneralException {
    private String type;
    private String id;
    private String tag;
    private char code;


    public MissingDataInRecordException(Class clazz, String recordId, String tag, char code) {
        super(makeMessage(clazz.getTypeName(), recordId, tag, code));
        this.type = clazz.getTypeName();
        this.id = recordId;
        this.tag = tag;
        this.code = code;
    }

    public Map<String, String> getFieldsAsMap() {
        Map<String, String> fields = new HashMap<>();
        fields.put("recordId", id);
        fields.put("tag", tag);
        fields.put("code", String.valueOf(code));

        return fields;
    }

    @Override
    public String toString() {
        return makeMessage(type, id, tag, code);
    }


    private static String makeMessage(String type, String id, String tag, char code) {
        return String.format("MissingDataInRecordException{type=%s, tag='%s', code='%s', id='%s'}", type, tag, code, id);
    }
}

