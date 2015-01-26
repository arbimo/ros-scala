package org.ros.scala.message;

import com.google.common.base.Preconditions;
import org.jboss.netty.buffer.ChannelBuffer;
import org.ros.internal.message.field.Field;
import org.ros.internal.message.field.FieldType;


/**
 * This class is copied from rosjava to make it public
 * @param <T>
 */
public class ValueField<T> extends Field {

    private T value;

    static <T> ValueField<T> newConstant(FieldType type, String name, T value) {
        return new ValueField<T>(type, name, value, true);
    }

    static <T> ValueField<T> newVariable(FieldType type, String name) {
        return new ValueField<T>(type, name, null, false);
    }

    private ValueField(FieldType type, String name, T value, boolean isConstant) {
        super(type, name, isConstant);
        this.value = value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getValue() {
        if (value == null) {
            setValue(type.getDefaultValue());
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) {
        Preconditions.checkNotNull(value);
        Preconditions.checkState(!isConstant);
        this.value = (T) value;
    }

    @Override
    public void serialize(ChannelBuffer buffer) {
        type.serialize(getValue(), buffer);
    }

    @Override
    public void deserialize(ChannelBuffer buffer) {
        Preconditions.checkState(!isConstant);
        setValue(type.<T>deserialize(buffer));
    }

    @Override
    public String getMd5String() {
        return String.format("%s %s\n", type, name);
    }

    @Override
    public String getJavaTypeName() {
        return type.getJavaTypeName();
    }

    @Override
    public String toString() {
        return "ValueField<" + type + ", " + name + ">";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((getValue() == null) ? 0 : getValue().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        Field other = (Field) obj;
        if (getValue() == null) {
            if (other.getValue() != null)
                return false;
        } else if (!getValue().equals(other.getValue()))
            return false;
        return true;
    }
}
