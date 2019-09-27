package com.app.producer.enums;

import com.app.producer.constants.AlarmConstants;
import com.app.producer.utils.Validator;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import java.util.stream.Collectors;

/**/
public enum AlarmType {

    ALARM_INGESTED("Ingested", "ingested"),
    ALARM_DISCARDED("Discarded", "discarded"),
    ALARM_PREDICTED("Predicted", "predicted");

    private  static final Set<String> ALL_ENUM_NAMES = Collections.unmodifiableSet(
            EnumSet.allOf(AlarmType.class).stream().map(AlarmType::name).collect(Collectors.toSet()));

    private static final Set<String> UI_TYPES = Collections.unmodifiableSet(EnumSet.allOf(AlarmType.class).stream()
                                                                                   .map(AlarmType::getUiType)
                                                                                   .collect(Collectors.toSet()));
    private static final Set<String> DB_TYPES = Collections.unmodifiableSet(EnumSet.allOf(AlarmType.class).stream()
                                                                                   .map(AlarmType::getDbType)
                                                                                   .collect(Collectors.toSet()));

    private final String dbType;
    private final String uiType;

    AlarmType(String dbValue, String uiValue) {
        Validator.assertNotBlank(dbValue, "dbValue");
        Validator.assertNotBlank(uiValue, "uiValue");
        this.dbType = dbValue;
        this.uiType = uiValue;
    }

    public String getDbType(){
        return dbType;
    }

    @JsonValue
    public String getUiType() {
        return uiType;
    }

    /**
     * Gets the enum type object that matches a given string type.
     * Used as a json creator by the deserializer.
     * Tries to match given string type with Enum names first, then with the enum type property if input is not a name.
     * @param value: string value to which we seek a matching enum. Could be enum name, or type property value.
     * @return Associated {@link AlarmType}.
     */
    @JsonCreator
    public static AlarmType factory(@JsonProperty(AlarmConstants.TICKET_TYPE) String value) {
        Validator.assertNotBlank(value, "value");
        if (ALL_ENUM_NAMES.contains(value)) {
            return AlarmType.valueOf(value);
        }
        return fromUiType(value);
    }

    public static AlarmType fromUiType(String value) {
        Validator.assertNotBlank(value, "Alarm type UI value");
        for (AlarmType type : AlarmType.values()) {
            if (type.getUiType().equalsIgnoreCase(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown UI value for " + AlarmType.class.getSimpleName() + " enum: " + value);
    }

    public static AlarmType fromDbType(String value) {
        Validator.assertNotBlank(value, "Alarm type DB value");
        for (AlarmType name : values()) {
            if (name.getDbType().equalsIgnoreCase(value)) {
                return name;
            }
        }
        throw new IllegalArgumentException("Unknown DB value for " + AlarmType.class.getSimpleName() + " enum: " + value);
    }

    public static Set<String> getUiTypes() {
        return UI_TYPES;
    }

    public static Set<String> getDbTypes() {
        return DB_TYPES;
    }
}
