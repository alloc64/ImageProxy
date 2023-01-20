package com.alloc64.imageproxy.dao.model;

import com.alloc64.imageproxy.utils.HashUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class BaseMediaEntity implements Serializable {
    private String key;
    @JsonIgnore
    private byte[] bytes;

    private String format;

    public <T extends BaseMediaEntity> T createKey() {
        setKey(HashUtils.sha256Base62(bytes));
        return (T) this;
    }
}
