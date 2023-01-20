package com.alloc64.imageproxy.dao.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Data
@SuperBuilder
@Jacksonized
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageEntity extends BaseMediaEntity {
    private Integer width;
    private Integer height;
}
